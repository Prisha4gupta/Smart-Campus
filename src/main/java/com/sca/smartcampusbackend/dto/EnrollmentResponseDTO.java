package com.sca.smartcampusbackend.dto;

import com.sca.smartcampusbackend.entity.StudentEnrollment.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for Student Enrollment responses
 * Used in GET requests to return enrollment data
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponseDTO {
    
    private Long id;
    private Long userId;
    private String username;  // Include username for convenience
    private CourseOfferingResponseDTO offering;
    private EnrollmentStatus status;
    private LocalDateTime enrolledAt;
}
