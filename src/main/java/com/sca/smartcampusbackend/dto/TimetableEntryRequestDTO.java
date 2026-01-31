package com.sca.smartcampusbackend.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

/**
 * DTO for creating/updating Timetable Entries
 * Used in POST/PUT requests to timetable entry endpoints
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableEntryRequestDTO {
    
    @NotNull(message = "Offering ID is required")
    private Long offeringId;
    
    @NotNull(message = "Day of week is required")
    @Min(value = 1, message = "Day of week must be between 1 and 7")
    @Max(value = 7, message = "Day of week must be between 1 and 7")
    private Integer dayOfWeek;  // 1=Sunday, 2=Monday, ..., 7=Saturday
    
    @NotNull(message = "Start time is required")
    private LocalTime startTime;
    
    @NotNull(message = "End time is required")
    private LocalTime endTime;
    
    @NotBlank(message = "Room is required")
    @Size(max = 50, message = "Room must not exceed 50 characters")
    private String room;
}
