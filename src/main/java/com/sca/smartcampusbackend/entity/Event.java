package com.sca.smartcampusbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Event Entity - Represents campus events and announcements (MVP version)
 * Maps to the 'events' table in the database
 * 
 * Future versions will support targeted events via groups and event_targets tables.
 * MVP: Simple public/private events with notification generation.
 * 
 * @since 1.0.0
 */
@Entity
@Table(name = "events")
@Data
public class Event {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String body;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    
    @Column(name = "is_public", nullable = false, columnDefinition = "TINYINT(1)")
    private Boolean isPublic = true;
    
    @Column(name = "start_datetime", nullable = false)
    private LocalDateTime startDatetime;
    
    @Column(name = "end_datetime")
    private LocalDateTime endDatetime;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * Automatically set creation timestamp
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
