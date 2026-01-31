package com.sca.smartcampusbackend.repository;

import com.sca.smartcampusbackend.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query("SELECT m FROM Message m " +
            "WHERE m.receiver.id = :userId OR m.sender.id = :userId OR m.isBroadcast = true " +
            "ORDER BY m.sentAt DESC")
    List<Message> findAllMessagesForUser(@Param("userId") Long userId);

    @Query("SELECT m FROM Message m " +
            "WHERE (m.sender.id = :user1 AND m.receiver.id = :user2) " +
            "   OR (m.sender.id = :user2 AND m.receiver.id = :user1) " +
            "ORDER BY m.sentAt ASC")
    List<Message> findConversation(@Param("user1") Long user1, @Param("user2") Long user2);

    @Query("SELECT m FROM Message m WHERE m.isBroadcast = true ORDER BY m.sentAt DESC")
    List<Message> findAllBroadcasts();

    @Query("SELECT m FROM Message m " +
            "WHERE m.receiver.id = :userId AND m.isRead = false")
    List<Message> findUnreadForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m " +
            "WHERE m.receiver.id = :userId AND m.isRead = false")
    long countUnreadForUser(@Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m " +
            "WHERE m.sender.id = :senderId AND m.receiver.id = :receiverId AND m.isRead = false")
    long countUnreadInConversation(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    @Modifying
    @Query("UPDATE Message m SET m.isRead = true " +
            "WHERE m.receiver.id = :userId AND m.sender.id = :senderId AND m.isRead = false")
    void markConversationAsRead(@Param("userId") Long userId, @Param("senderId") Long senderId);

    @Query("SELECT DISTINCT m.sender.id FROM Message m WHERE m.receiver.id = :userId " +
            "UNION " +
            "SELECT DISTINCT m.receiver.id FROM Message m WHERE m.sender.id = :userId AND m.receiver IS NOT NULL")
    List<Long> findConversationPartnerIds(@Param("userId") Long userId);
}
