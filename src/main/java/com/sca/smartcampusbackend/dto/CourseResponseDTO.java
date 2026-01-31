package com.sca.smartcampusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for Course responses
 * Used in GET requests to return course data
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseResponseDTO {
    
    private Integer id;
    private String code;
    private String title;
    private Integer credits;
    private String description;
    private LocalDateTime createdAt;
}
