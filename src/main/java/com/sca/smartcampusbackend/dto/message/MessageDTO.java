package com.sca.smartcampusbackend.dto.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private Long id;
    private Long senderId;
    private String senderUsername;
    private Long receiverId;
    private String receiverUsername;
    private String content;
    private LocalDateTime sentAt;
    private Boolean isRead;
    private Boolean isBroadcast;
}
