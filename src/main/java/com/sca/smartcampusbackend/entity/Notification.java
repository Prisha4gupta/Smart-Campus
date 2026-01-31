package com.sca.smartcampusbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * Notification Entity - Represents per-user notifications
 * Maps to the 'notifications' table in the database
 * 
 * Supports idempotent notification generation via unique constraint on (user_id, event_id).
 * Batch insert capable - designed for 500+ row bulk operations.
 * 
 * @since 1.0.0
 */
@Entity
@Table(name = "notifications",
       uniqueConstraints = @UniqueConstraint(
           name = "unique_user_event",
           columnNames = {"user_id", "event_id"}
       ))
@Data
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;  // Nullable - notifications can exist without events
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT")
    private String body;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "sent_at")
    private LocalDateTime sentAt;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "delivery_channel", nullable = false)
    private DeliveryChannel deliveryChannel = DeliveryChannel.IN_APP;
    
    /**
     * Automatically set creation timestamp
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
    
    /**
     * Delivery channel enum for notification routing
     */
    public enum DeliveryChannel {
        IN_APP,
        EMAIL,
        PUSH
    }
}
