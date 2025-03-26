package com.midou.tutorial.backlog.dto.taskDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTaskDescriptionDTO {
    private long taskId;
    private String description;
}
