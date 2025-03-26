package com.midou.tutorial.backlog.dto.taskDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateTaskDTO {
    private String title;
    private String label;
    private long backlogId;
    private long sprintId;
}
