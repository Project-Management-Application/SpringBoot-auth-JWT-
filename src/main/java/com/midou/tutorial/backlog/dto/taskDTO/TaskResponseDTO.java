package com.midou.tutorial.backlog.dto.taskDTO;

import com.midou.tutorial.backlog.enums.Label;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDTO {
    private long taskId;
    private String title;
    private Label label;
    private long backlogId;
    private long sprintId;
}
