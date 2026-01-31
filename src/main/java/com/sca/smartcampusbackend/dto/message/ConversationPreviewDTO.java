package com.sca.smartcampusbackend.dto.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConversationPreviewDTO {
    private Long otherUserId;
    private String otherUsername;
    private String otherRole;
    private String lastMessage;
    private LocalDateTime lastMessageTime;
    private Long unreadCount;
}
