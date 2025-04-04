package com.midou.tutorial.backlog.dto.taskDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class moveTaskDTO {
    private Long taskId;
    private String senderType;
    private Long senderId;
    private String receiverType;
    private Long receiverId;
}
