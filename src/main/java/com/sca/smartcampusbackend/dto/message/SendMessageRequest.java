package com.sca.smartcampusbackend.dto.message;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequest {
    private Long receiverId;
    private List<Long> receiverIds;
    private String content;
    private Boolean isBroadcast = false;
}
