package com.sca.smartcampusbackend.service.impl;

import com.sca.smartcampusbackend.dto.message.ConversationPreviewDTO;
import com.sca.smartcampusbackend.dto.message.MessageDTO;
import com.sca.smartcampusbackend.entity.Message;
import com.sca.smartcampusbackend.entity.User;
import com.sca.smartcampusbackend.repository.MessageRepository;
import com.sca.smartcampusbackend.repository.UserRepository;
import com.sca.smartcampusbackend.service.MessageService;
import com.sca.smartcampusbackend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public MessageDTO sendToUser(Long senderId, Long receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setContent(content);
        message.setIsRead(false);
        message.setIsBroadcast(false);

        Message saved = messageRepository.save(message);

        // Create notification for receiver
        notificationService.notifyUser(
                receiverId,
                "New Message",
                "You received a message from " + sender.getUsername());

        return toDTO(saved);
    }

    @Override
    @Transactional
    public void sendToMany(Long senderId, List<Long> receiverIds, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        for (Long receiverId : receiverIds) {
            User receiver = userRepository.findById(receiverId).orElse(null);
            if (receiver != null) {
                Message message = new Message();
                message.setSender(sender);
                message.setReceiver(receiver);
                message.setContent(content);
                message.setIsRead(false);
                message.setIsBroadcast(false);
                messageRepository.save(message);

                // Create notification for each receiver
                notificationService.notifyUser(
                        receiverId,
                        "New Message",
                        "Admin sent you a message");
            }
        }
    }

    @Override
    @Transactional
    public void sendBroadcast(Long senderId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(null);
        message.setContent(content);
        message.setIsRead(false);
        message.setIsBroadcast(true);
        messageRepository.save(message);

        // Notify all students about the broadcast
        List<User> students = userRepository.findAll().stream()
                .filter(u -> "STUDENT".equals(u.getRole()))
                .toList();

        for (User student : students) {
            notificationService.notifyUser(
                    student.getId(),
                    "Announcement",
                    "Admin posted an announcement");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ConversationPreviewDTO> getInbox(Long userId) {
        List<Long> partnerIds = messageRepository.findConversationPartnerIds(userId);
        List<ConversationPreviewDTO> inbox = new ArrayList<>();

        for (Long partnerId : partnerIds) {
            if (partnerId == null || partnerId.equals(userId))
                continue;

            User partner = userRepository.findById(partnerId).orElse(null);
            if (partner == null)
                continue;

            List<Message> conversation = messageRepository.findConversation(userId, partnerId);
            if (conversation.isEmpty())
                continue;

            Message lastMessage = conversation.get(conversation.size() - 1);
            long unread = messageRepository.countUnreadInConversation(partnerId, userId);

            ConversationPreviewDTO dto = new ConversationPreviewDTO();
            dto.setOtherUserId(partnerId);
            dto.setOtherUsername(partner.getUsername());
            dto.setOtherRole(partner.getRole());
            dto.setLastMessage(lastMessage.getContent().length() > 50
                    ? lastMessage.getContent().substring(0, 50) + "..."
                    : lastMessage.getContent());
            dto.setLastMessageTime(lastMessage.getSentAt());
            dto.setUnreadCount(unread);

            inbox.add(dto);
        }

        inbox.sort((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()));
        return inbox;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> getConversation(Long userId, Long otherId) {
        return messageRepository.findConversation(userId, otherId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageDTO> getBroadcasts() {
        return messageRepository.findAllBroadcasts().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId).orElse(null);
        if (message != null && !message.getIsRead()) {
            message.setIsRead(true);
            messageRepository.save(message);
        }
    }

    @Override
    @Transactional
    public void markConversationAsRead(Long userId, Long senderId) {
        messageRepository.markConversationAsRead(userId, senderId);
    }

    @Override
    @Transactional(readOnly = true)
    public long getUnreadCount(Long userId) {
        return messageRepository.countUnreadForUser(userId);
    }

    private MessageDTO toDTO(Message m) {
        MessageDTO dto = new MessageDTO();
        dto.setId(m.getId());
        dto.setSenderId(m.getSender().getId());
        dto.setSenderUsername(m.getSender().getUsername());
        if (m.getReceiver() != null) {
            dto.setReceiverId(m.getReceiver().getId());
            dto.setReceiverUsername(m.getReceiver().getUsername());
        }
        dto.setContent(m.getContent());
        dto.setSentAt(m.getSentAt());
        dto.setIsRead(m.getIsRead());
        dto.setIsBroadcast(m.getIsBroadcast());
        return dto;
    }
}
