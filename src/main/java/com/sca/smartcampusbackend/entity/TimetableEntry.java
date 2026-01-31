package com.sca.smartcampusbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalTime;

/**
 * TimetableEntry Entity - Represents actual meeting times for course offerings
 * Maps to the 'timetable_entries' table in the database
 * 
 * Each course offering can have multiple meeting times throughout the week.
 * Students see their personalized timetable by joining this with their enrollments.
 * 
 * @since 1.0.0
 */
@Entity
@Table(name = "timetable_entries")
@Data
public class TimetableEntry {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offering_id", nullable = false)
    private CourseOffering offering;
    
    @Column(name = "day_of_week", nullable = false, columnDefinition = "TINYINT")
    private Integer dayOfWeek;  // 1=Sunday, 2=Monday, ..., 7=Saturday
    
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;
    
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;
    
    @Column(nullable = false, length = 50)
    private String room;
    
    /**
     * Validate day of week before persisting
     */
    @PrePersist
    @PreUpdate
    protected void validate() {
        if (dayOfWeek < 1 || dayOfWeek > 7) {
            throw new IllegalArgumentException("dayOfWeek must be between 1 and 7");
        }
        if (startTime != null && endTime != null && !startTime.isBefore(endTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }
    }
}
