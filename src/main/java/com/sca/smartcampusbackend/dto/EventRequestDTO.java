package com.sca.smartcampusbackend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for creating/updating Events
 * Used in POST/PUT requests to event endpoints
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestDTO {
    
    @NotBlank(message = "Event title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Size(max = 5000, message = "Body must not exceed 5000 characters")
    private String body;
    
    private Boolean isPublic = true;
    
    @NotNull(message = "Start datetime is required")
    @FutureOrPresent(message = "Start datetime must be today or in the future")
    private LocalDateTime startDatetime;
    
    private LocalDateTime endDatetime;
    
    // createdBy will be set from authenticated user in controller
}
