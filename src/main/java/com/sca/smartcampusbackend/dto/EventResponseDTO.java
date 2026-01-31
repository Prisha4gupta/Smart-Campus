package com.sca.smartcampusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for Event responses (already exists but updated for new schema)
 * Used in GET requests to return event data
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {
    
    private Long id;
    private String title;
    private String body;
    private String createdByUsername;
    private Boolean isPublic;
    private LocalDateTime startDatetime;
    private LocalDateTime endDatetime;
    private LocalDateTime createdAt;
}
