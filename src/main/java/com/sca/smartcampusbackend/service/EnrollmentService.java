package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.EnrollmentRequestDTO;
import com.sca.smartcampusbackend.dto.EnrollmentResponseDTO;

import java.util.List;

/**
 * Service interface for Student Enrollment operations
 * Handles enrollment logic including capacity checks and waitlist management
 * 
 * @since 1.0.0
 */
public interface EnrollmentService {
    
    /**
     * Enroll a student in a course offering
     * Checks capacity and assigns to waitlist if full
     * 
     * @param dto Enrollment request DTO
     * @return Created enrollment response DTO
     */
    EnrollmentResponseDTO enrollStudent(EnrollmentRequestDTO dto);
    
    /**
     * Drop a student from a course offering
     * Updates status to DROPPED
     * 
     * @param enrollmentId Enrollment ID
     */
    void dropEnrollment(Long enrollmentId);
    
    /**
     * Get all enrollments for a student
     * @param userId User ID
     * @return List of enrollments
     */
    List<EnrollmentResponseDTO> getEnrollmentsByStudent(Long userId);
    
    /**
     * Get all enrollments for an offering
     * @param offeringId Offering ID
     * @return List of enrollments
     */
    List<EnrollmentResponseDTO> getEnrollmentsByOffering(Long offeringId);
    
    /**
     * Get active (enrolled) courses for a student
     * @param userId User ID
     * @return List of active enrollments
     */
    List<EnrollmentResponseDTO> getActiveEnrollments(Long userId);
    
    /**
     * Get enrollment count for an offering
     * @param offeringId Offering ID
     * @return Count of enrolled students
     */
    long getEnrollmentCount(Long offeringId);
    
    /**
     * Check if offering is full
     * @param offeringId Offering ID
     * @return true if full
     */
    boolean isOfferingFull(Long offeringId);
}
