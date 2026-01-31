package com.sca.smartcampusbackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalTime;

/**
 * DTO for Timetable Entry responses
 * Used in GET requests to return timetable data
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TimetableEntryResponseDTO {
    
    private Long id;
    private Long offeringId;
    private String courseCode;
    private String courseTitle;
    private String facultyName;
    private Integer dayOfWeek;
    private String dayName;  // e.g., "Monday"
    private LocalTime startTime;
    private LocalTime endTime;
    private String room;
}
