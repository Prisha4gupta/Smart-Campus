package com.sca.smartcampusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for Course Offering responses
 * Used in GET requests to return offering data with nested course/faculty info
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseOfferingResponseDTO {
    
    private Long id;
    private String semester;
    private String sectionCode;
    private Integer capacity;
    private LocalDateTime createdAt;
    
    // Nested course information
    private CourseResponseDTO course;
    
    // Nested faculty information
    private FacultyResponseDTO faculty;
    
    // Enrollment count (optional - can be added in service layer)
    private Integer enrolledCount;
}
