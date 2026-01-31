package com.sca.smartcampusbackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating Course Offerings
 * Used in POST/PUT requests to offering endpoints
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseOfferingRequestDTO {
    
    @NotNull(message = "Course ID is required")
    private Integer courseId;
    
    @NotBlank(message = "Semester is required")
    @Size(max = 50, message = "Semester must not exceed 50 characters")
    private String semester;  // e.g., "Fall2024", "Spring2025"
    
    @NotBlank(message = "Section code is required")
    @Size(max = 10, message = "Section code must not exceed 10 characters")
    private String sectionCode;  // e.g., "A", "B", "L1"
    
    @NotNull(message = "Faculty ID is required")
    private Long facultyId;
    
    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;
}
