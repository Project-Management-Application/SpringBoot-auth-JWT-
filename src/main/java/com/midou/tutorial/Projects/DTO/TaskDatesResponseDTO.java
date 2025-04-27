package com.midou.tutorial.Projects.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class TaskDatesResponseDTO {
    private Long taskId;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime dueDateReminder;
    private String message;
}