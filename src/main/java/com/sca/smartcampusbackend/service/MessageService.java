package com.sca.smartcampusbackend.service;

import com.sca.smartcampusbackend.dto.message.ConversationPreviewDTO;
import com.sca.smartcampusbackend.dto.message.MessageDTO;

import java.util.List;

public interface MessageService {
    MessageDTO sendToUser(Long senderId, Long receiverId, String content);

    void sendToMany(Long senderId, List<Long> receiverIds, String content);

    void sendBroadcast(Long senderId, String content);

    List<ConversationPreviewDTO> getInbox(Long userId);

    List<MessageDTO> getConversation(Long userId, Long otherId);

    List<MessageDTO> getBroadcasts();

    void markAsRead(Long messageId);

    void markConversationAsRead(Long userId, Long senderId);

    long getUnreadCount(Long userId);
}
