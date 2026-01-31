package com.sca.smartcampusbackend.controller;

import com.sca.smartcampusbackend.dto.EnrollmentRequestDTO;
import com.sca.smartcampusbackend.dto.EnrollmentResponseDTO;
import com.sca.smartcampusbackend.service.EnrollmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Student Enrollment operations
 * Provides endpoints for managing student course registrations
 * 
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
public class EnrollmentController {
    
    private final EnrollmentService enrollmentService;
    
    /**
     * Enroll a student in a course offering
     * POST /api/enrollments
     * 
     * Automatically checks capacity and assigns to waitlist if full
     */
    @PostMapping
    public ResponseEntity<EnrollmentResponseDTO> enrollStudent(@Valid @RequestBody EnrollmentRequestDTO dto) {
        EnrollmentResponseDTO created = enrollmentService.enrollStudent(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    /**
     * Drop an enrollment (sets status to DROPPED)
     * DELETE /api/enrollments/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> dropEnrollment(@PathVariable Long id) {
        enrollmentService.dropEnrollment(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Get all enrollments for a student
     * GET /api/enrollments/student/{userId}
     */
    @GetMapping("/student/{userId}")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByStudent(@PathVariable Long userId) {
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getEnrollmentsByStudent(userId);
        return ResponseEntity.ok(enrollments);
    }
    
    /**
     * Get all enrollments for an offering (Admin only)
     * GET /api/enrollments/offering/{offeringId}
     */
    @GetMapping("/offering/{offeringId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EnrollmentResponseDTO>> getEnrollmentsByOffering(@PathVariable Long offeringId) {
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getEnrollmentsByOffering(offeringId);
        return ResponseEntity.ok(enrollments);
    }
    
    /**
     * Get active enrollments for a student
     * GET /api/enrollments/student/{userId}/active
     */
    @GetMapping("/student/{userId}/active")
    public ResponseEntity<List<EnrollmentResponseDTO>> getActiveEnrollments(@PathVariable Long userId) {
        List<EnrollmentResponseDTO> enrollments = enrollmentService.getActiveEnrollments(userId);
        return ResponseEntity.ok(enrollments);
    }
    
    /**
     * Get enrollment count for an offering
     * GET /api/enrollments/offering/{offeringId}/count
     */
    @GetMapping("/offering/{offeringId}/count")
    public ResponseEntity<Long> getEnrollmentCount(@PathVariable Long offeringId) {
        long count = enrollmentService.getEnrollmentCount(offeringId);
        return ResponseEntity.ok(count);
    }
    
    /**
     * Check if offering is full
     * GET /api/enrollments/offering/{offeringId}/is-full
     */
    @GetMapping("/offering/{offeringId}/is-full")
    public ResponseEntity<Boolean> isOfferingFull(@PathVariable Long offeringId) {
        boolean isFull = enrollmentService.isOfferingFull(offeringId);
        return ResponseEntity.ok(isFull);
    }
}
