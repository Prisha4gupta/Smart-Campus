package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.CourseOfferingResponseDTO;
import com.sca.smartcampusbackend.service.NotificationService;
import com.sca.smartcampusbackend.dto.EnrollmentRequestDTO;
import com.sca.smartcampusbackend.dto.EnrollmentResponseDTO;
import com.sca.smartcampusbackend.entity.CourseOffering;
import com.sca.smartcampusbackend.entity.StudentEnrollment;
import com.sca.smartcampusbackend.entity.StudentEnrollment.EnrollmentStatus;
import com.sca.smartcampusbackend.entity.User;
import com.sca.smartcampusbackend.repository.CourseOfferingRepository;
import com.sca.smartcampusbackend.repository.StudentEnrollmentRepository;
import com.sca.smartcampusbackend.repository.UserRepository;
import com.sca.smartcampusbackend.service.CourseOfferingService;
import com.sca.smartcampusbackend.service.EnrollmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of EnrollmentService
 * Handles student enrollment logic with capacity checks and waitlist management
 * 
 * @since 1.0.0
 */
@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements EnrollmentService {

    private final StudentEnrollmentRepository enrollmentRepository;
    private final NotificationService notificationService;
    private final UserRepository userRepository;
    private final CourseOfferingRepository offeringRepository;
    private final CourseOfferingService offeringService;

    @Override
    @Transactional
    public EnrollmentResponseDTO enrollStudent(EnrollmentRequestDTO dto) {
        // Validate user exists
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + dto.getUserId()));

        // Validate offering exists
        CourseOffering offering = offeringRepository.findById(dto.getOfferingId())
                .orElseThrow(() -> new IllegalArgumentException("Offering not found with id: " + dto.getOfferingId()));

        try {
            // Check if already enrolled
            Optional<StudentEnrollment> existing = enrollmentRepository.findByUser_IdAndOffering_Id(
                    dto.getUserId(), dto.getOfferingId());

            if (existing.isPresent()) {
                StudentEnrollment enrollment = existing.get();
                if (enrollment.getStatus() == EnrollmentStatus.ENROLLED) {
                    throw new IllegalArgumentException("Student is already enrolled in this offering");
                } else if (enrollment.getStatus() == EnrollmentStatus.WAITLIST) {
                    throw new IllegalArgumentException("Student is already on the waitlist for this offering");
                }
                // If DROPPED, allow re-enrollment
            }

            // Check capacity
            long enrolledCount = enrollmentRepository.countByOffering_IdAndStatus(
                    dto.getOfferingId(), EnrollmentStatus.ENROLLED);

            StudentEnrollment enrollment = new StudentEnrollment();
            enrollment.setUser(user);
            enrollment.setOffering(offering);

            if (enrolledCount >= offering.getCapacity()) {
                // Add to waitlist
                enrollment.setStatus(EnrollmentStatus.WAITLIST);
            } else {
                // Enroll normally
                enrollment.setStatus(EnrollmentStatus.ENROLLED);
            }

            StudentEnrollment saved = enrollmentRepository.save(enrollment);
            enrollmentRepository.flush(); // Ensure persistence before notification

            // Success notification
            try {
                String title = "Enrollment Confirmed";
                String body = offering.getCourse().getCode() + " - " + offering.getCourse().getTitle();
                notificationService.createNotification(user.getId(), null, title, body);
            } catch (Exception e) {
                // Log but don't rollback
                System.err.println("Failed to send enrollment notification: " + e.getMessage());
            }

            return toResponseDTO(saved);
        } catch (IllegalArgumentException e) {
            // Failure notification attempted but safe to fail
            try {
                String title = "Enrollment Failed";
                String body = e.getMessage();
                notificationService.createNotification(user.getId(), null, title, body);
            } catch (Exception ex) {
                System.err.println("Failed to send failure notification: " + ex.getMessage());
            }
            throw e;
        }
    }

    @Override
    @Transactional
    public void dropEnrollment(Long enrollmentId) {
        StudentEnrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new IllegalArgumentException("Enrollment not found with id: " + enrollmentId));

        enrollment.setStatus(EnrollmentStatus.DROPPED);
        enrollmentRepository.save(enrollment);
        enrollmentRepository.flush(); // Ensure persistence before notification

        // Notification for drop
        try {
            String title = "Enrollment Dropped";
            String body = enrollment.getOffering().getCourse().getCode() + " - "
                    + enrollment.getOffering().getCourse().getTitle();
            notificationService.createNotification(enrollment.getUser().getId(), null, title, body);
        } catch (Exception e) {
            System.err.println("Failed to send drop notification: " + e.getMessage());
        }

        // TODO: Future enhancement - automatically move waitlisted student to enrolled
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getEnrollmentsByStudent(Long userId) {
        return enrollmentRepository.findByUser_Id(userId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getEnrollmentsByOffering(Long offeringId) {
        return enrollmentRepository.findByOffering_Id(offeringId).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<EnrollmentResponseDTO> getActiveEnrollments(Long userId) {
        return enrollmentRepository.findByUser_IdAndStatus(userId, EnrollmentStatus.ENROLLED).stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long getEnrollmentCount(Long offeringId) {
        return enrollmentRepository.countByOffering_IdAndStatus(offeringId, EnrollmentStatus.ENROLLED);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isOfferingFull(Long offeringId) {
        CourseOffering offering = offeringRepository.findById(offeringId)
                .orElseThrow(() -> new IllegalArgumentException("Offering not found with id: " + offeringId));

        long enrolledCount = enrollmentRepository.countByOffering_IdAndStatus(
                offeringId, EnrollmentStatus.ENROLLED);

        return enrolledCount >= offering.getCapacity();
    }

    /**
     * Convert StudentEnrollment entity to response DTO
     */
    private EnrollmentResponseDTO toResponseDTO(StudentEnrollment enrollment) {
        CourseOfferingResponseDTO offeringDTO = offeringService.getOfferingById(enrollment.getOffering().getId());

        return new EnrollmentResponseDTO(
                enrollment.getId(),
                enrollment.getUser().getId(),
                enrollment.getUser().getUsername(),
                offeringDTO,
                enrollment.getStatus(),
                enrollment.getEnrolledAt());
    }
}
