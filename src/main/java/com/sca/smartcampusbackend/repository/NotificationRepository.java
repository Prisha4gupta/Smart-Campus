package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Notification entity
 * Provides database access methods for notification operations
 * Supports batch operations for efficient notification generation
 * 
 * @author Team Stack Underflow
 * @since 1.0.0
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a specific user
     * @param userId User ID
     * @return List of notifications ordered by creation time (newest first)
     */
    @Query("SELECT n FROM Notification n " +
           "WHERE n.user.id = :userId " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findByUser_IdOrderByCreatedAtDesc(@Param("userId") Long userId);
    
    /**
     * Find unread notifications for a user
     * @param userId User ID
     * @return List of unread notifications
     */
    @Query("SELECT n FROM Notification n " +
           "WHERE n.user.id = :userId " +
           "AND n.readAt IS NULL " +
           "ORDER BY n.createdAt DESC")
    List<Notification> findUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * Find notifications by event
     * @param eventId Event ID
     * @return List of notifications for the event
     */
    List<Notification> findByEvent_Id(Long eventId);
    
    /**
     * Count unread notifications for a user
     * @param userId User ID
     * @return Count of unread notifications
     */
    @Query("SELECT COUNT(n) FROM Notification n " +
           "WHERE n.user.id = :userId " +
           "AND n.readAt IS NULL")
    long countUnreadByUserId(@Param("userId") Long userId);
    
    /**
     * Mark notification as read
     * @param notificationId Notification ID
     * @param readAt Read timestamp
     */
    @Modifying
    @Query("UPDATE Notification n " +
           "SET n.readAt = :readAt " +
           "WHERE n.id = :notificationId")
    void markAsRead(
        @Param("notificationId") Long notificationId,
        @Param("readAt") LocalDateTime readAt
    );
    
    /**
     * Mark all notifications for a user as read
     * @param userId User ID
     * @param readAt Read timestamp
     */
    @Modifying
    @Query("UPDATE Notification n " +
           "SET n.readAt = :readAt " +
           "WHERE n.user.id = :userId " +
           "AND n.readAt IS NULL")
    void markAllAsReadForUser(
        @Param("userId") Long userId,
        @Param("readAt") LocalDateTime readAt
    );
}
