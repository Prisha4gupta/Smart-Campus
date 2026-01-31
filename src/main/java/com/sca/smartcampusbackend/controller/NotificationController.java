
package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.NotificationResponseDTO;
import com.sca.smartcampusbackend.service.NotificationService;
import com.sca.smartcampusbackend.repository.UserRepository;
import com.sca.smartcampusbackend.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @PostMapping("/generate")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Integer>> generateNotificationsForOffering(
            @RequestBody Map<String, Object> request) {
        Long eventId = request.get("eventId") != null ? Long.valueOf(request.get("eventId").toString()) : null;
        Long offeringId = Long.valueOf(request.get("offeringId").toString());
        String title = request.get("title").toString();
        String body = request.get("body").toString();
        int count = notificationService.generateNotificationsForOffering(eventId, offeringId, title, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("notificationsCreated", count));
    }

    @PostMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NotificationResponseDTO> createNotification(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> request) {
        Long eventId = request.get("eventId") != null ? Long.valueOf(request.get("eventId").toString()) : null;
        String title = request.get("title").toString();
        String body = request.get("body").toString();
        NotificationResponseDTO created = notificationService.createNotification(userId, eventId, title, body);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getNotifications() {
        Long userId = getCurrentUserId();
        List<NotificationResponseDTO> notifications = notificationService.getNotificationsByUser(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotifications() {
        Long userId = getCurrentUserId();
        List<NotificationResponseDTO> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // NEW: Unread notifications by user ID path variable (for frontend
    // compatibility)
    @GetMapping("/user/{userId}/unread")
    public ResponseEntity<List<NotificationResponseDTO>> getUnreadNotificationsByUserId(@PathVariable Long userId) {
        List<NotificationResponseDTO> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount() {
        Long userId = getCurrentUserId();
        long count = notificationService.getUnreadCount(userId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/read-all")
    public ResponseEntity<Void> markAllAsRead() {
        Long userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // NEW: Mark all as read by user ID path variable (for frontend compatibility)
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsReadByUserId(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = ((UserDetails) auth.getPrincipal()).getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));
        return user.getId();
    }
}
