package com.midou.tutorial.Projects.DTO;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class SetTaskDatesRequestDTO {
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime dueDateReminder;
}