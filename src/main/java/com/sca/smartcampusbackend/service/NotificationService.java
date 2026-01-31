package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.NotificationResponseDTO;

import java.util.List;

/**
 * Service interface for Notification operations
 * Supports idempotent batch notification generation
 * 
 * @since 1.0.0
 */
public interface NotificationService {

    /**
     * Generate notifications for all students enrolled in a specific offering
     * Uses idempotent batch insertion (500 rows per batch)
     * 
     * @param eventId    Event ID to link notifications to
     * @param offeringId Course offering ID to target
     * @param title      Notification title
     * @param body       Notification body
     * @return Count of notifications created
     */
    int generateNotificationsForOffering(Long eventId, Long offeringId, String title, String body);

    /**
     * Generate notification for a single user
     * 
     * @param userId  User ID
     * @param eventId Event ID (nullable)
     * @param title   Notification title
     * @param body    Notification body
     * @return Created notification
     */
    NotificationResponseDTO createNotification(Long userId, Long eventId, String title, String body);

    /**
     * Get all notifications for a user
     * 
     * @param userId User ID
     * @return List of notifications (newest first)
     */
    List<NotificationResponseDTO> getNotificationsByUser(Long userId);

    /**
     * Get unread notifications for a user
     * 
     * @param userId User ID
     * @return List of unread notifications
     */
    List<NotificationResponseDTO> getUnreadNotifications(Long userId);

    /**
     * Mark notification as read
     * 
     * @param notificationId Notification ID
     */
    void markAsRead(Long notificationId);

    /**
     * Mark all notifications for a user as read
     * 
     * @param userId User ID
     */
    void markAllAsRead(Long userId);

    /**
     * Get unread count for a user
     * 
     * @param userId User ID
     * @return Count of unread notifications
     */
    long getUnreadCount(Long userId);

    /**
     * Create a simple notification for messaging (no event association)
     * 
     * @param userId  User ID
     * @param title   Notification title
     * @param message Notification body
     */
    void notifyUser(Long userId, String title, String message);
}
