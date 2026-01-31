package com.sca.smartcampusbackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating Student Enrollments
 * Used in POST requests to enrollment endpoints
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentRequestDTO {
    
    @NotNull(message = "User ID is required")
    private Long userId;
    
    @NotNull(message = "Offering ID is required")
    private Long offeringId;
}
