package com.sca.smartcampusbackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for creating/updating Courses
 * Used in POST/PUT requests to course endpoints
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseRequestDTO {
    
    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must not exceed 20 characters")
    private String code;
    
    @NotBlank(message = "Course title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @NotNull(message = "Credits is required")
    @Min(value = 1, message = "Credits must be at least 1")
    private Integer credits;
    
    @Size(max = 5000, message = "Description must not exceed 5000 characters")
    private String description;
}
