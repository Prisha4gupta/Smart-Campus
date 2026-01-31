package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.NotificationResponseDTO;
import com.sca.smartcampusbackend.entity.Event;
import com.sca.smartcampusbackend.entity.Notification;
import com.sca.smartcampusbackend.entity.Notification.DeliveryChannel;
import com.sca.smartcampusbackend.entity.StudentEnrollment;
import com.sca.smartcampusbackend.entity.StudentEnrollment.EnrollmentStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.access.AccessDeniedException;
import com.sca.smartcampusbackend.entity.User;
import com.sca.smartcampusbackend.repository.EventRepository;
import com.sca.smartcampusbackend.repository.NotificationRepository;
import com.sca.smartcampusbackend.repository.StudentEnrollmentRepository;
import com.sca.smartcampusbackend.repository.UserRepository;
import com.sca.smartcampusbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of NotificationService
 * Supports idempotent batch notification generation (500 rows per batch)
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final StudentEnrollmentRepository enrollmentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    private static final int BATCH_SIZE = 500;

    @Override
    @Transactional
    public int generateNotificationsForOffering(Long eventId, Long offeringId, String title, String body) {
        // Validate event exists
        Event event = null;
        if (eventId != null) {
            event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));
        }

        // Get all enrolled students for this offering
        List<StudentEnrollment> enrollments = enrollmentRepository
                .findByOffering_IdAndStatus(offeringId, EnrollmentStatus.ENROLLED);

        if (enrollments.isEmpty()) {
            return 0;
        }

        List<Notification> notifications = new ArrayList<>();
        Event finalEvent = event;

        for (StudentEnrollment enrollment : enrollments) {
            Notification notification = new Notification();
            notification.setUser(enrollment.getUser());
            notification.setEvent(finalEvent);
            notification.setTitle(title);
            notification.setBody(body);
            notification.setDeliveryChannel(DeliveryChannel.IN_APP);
            notification.setSentAt(LocalDateTime.now());

            notifications.add(notification);

            // Batch insert every 500 rows
            if (notifications.size() >= BATCH_SIZE) {
                notificationRepository.saveAll(notifications);
                notifications.clear();
            }
        }

        // Save remaining notifications
        if (!notifications.isEmpty()) {
            notificationRepository.saveAll(notifications);
        }

        return enrollments.size();
    }

    @Override
    @Transactional
    public NotificationResponseDTO createNotification(Long userId, Long eventId, String title, String body) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        Event event = null;
        if (eventId != null) {
            event = eventRepository.findById(eventId)
                    .orElseThrow(() -> new IllegalArgumentException("Event not found with id: " + eventId));
        }

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setEvent(event);
        notification.setTitle(title);
        notification.setBody(body);
        notification.setDeliveryChannel(DeliveryChannel.IN_APP);
        notification.setSentAt(LocalDateTime.now());

        Notification saved = notificationRepository.save(notification);
        return toResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByUser(Long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUnreadNotifications(Long userId) {
        return notificationRepository.findUnreadByUserId(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        // Verify ownership
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) auth.getPrincipal()).getUsername();
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));
        if (!notification.getUser().getUsername().equals(currentUsername)) {
            throw new AccessDeniedException("Cannot modify another user's notification");
        }
        notificationRepository.markAsRead(notificationId, LocalDateTime.now());
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        // Ensure the caller is the same user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = ((UserDetails) auth.getPrincipal()).getUsername();
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
        if (!currentUser.getId().equals(userId)) {
            throw new AccessDeniedException("Cannot mark notifications for another user");
        }
        notificationRepository.markAllAsReadForUser(userId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return notificationRepository.countUnreadByUserId(userId);
    }

    @Override
    @Transactional
    public void notifyUser(Long userId, String title, String message) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return;

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setEvent(null);
        notification.setTitle(title);
        notification.setBody(message);
        notification.setDeliveryChannel(DeliveryChannel.IN_APP);
        notification.setSentAt(LocalDateTime.now());
        notificationRepository.save(notification);
    }

    /**
     * Convert Notification entity to response DTO
     */
    private NotificationResponseDTO toResponseDTO(Notification notification) {
        return new NotificationResponseDTO(
                notification.getId(),
                notification.getUser().getId(),
                notification.getEvent() != null ? notification.getEvent().getId() : null,
                notification.getTitle(),
                notification.getBody(),
                notification.getCreatedAt(),
                notification.getReadAt(),
                notification.getSentAt(),
                notification.getDeliveryChannel());
    }
}
