package com.sca.smartcampusbackend.dto;

import com.sca.smartcampusbackend.entity.Notification.DeliveryChannel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * DTO for Notification responses
 * Used in GET requests to return notification data
 * 
 * @since 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponseDTO {
    
    private Long id;
    private Long userId;
    private Long eventId;  // Nullable
    private String title;
    private String body;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;
    private LocalDateTime sentAt;
    private DeliveryChannel deliveryChannel;
    private Boolean isRead;  // Computed field
    
    /**
     * Constructor that automatically computes isRead
     */
    public NotificationResponseDTO(Long id, Long userId, Long eventId, String title, String body,
                                   LocalDateTime createdAt, LocalDateTime readAt, LocalDateTime sentAt,
                                   DeliveryChannel deliveryChannel) {
        this.id = id;
        this.userId = userId;
        this.eventId = eventId;
        this.title = title;
        this.body = body;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.sentAt = sentAt;
        this.deliveryChannel = deliveryChannel;
        this.isRead = readAt != null;
    }
}
