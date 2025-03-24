package com.midou.tutorial.backlog.services;

import com.midou.tutorial.backlog.dto.CreateSprintDTO;
import com.midou.tutorial.backlog.dto.CreateTaskDTO;
import com.midou.tutorial.backlog.entities.Backlog;
import com.midou.tutorial.backlog.entities.Sprint;
import com.midou.tutorial.backlog.entities.Task.Task;
import com.midou.tutorial.backlog.enums.Label;
import com.midou.tutorial.backlog.repositories.BacklogRepository;
import com.midou.tutorial.backlog.repositories.SprintRepository;
import com.midou.tutorial.backlog.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BacklogService {
    private final BacklogRepository backlogRepository;
    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;

    public void createBacklog() {
        var backlog = Backlog.builder()
                .build();
        backlogRepository.save(backlog);
    }

    public void createSprint(CreateSprintDTO sprint) {
        Backlog backlog = backlogRepository.findById(sprint.getBacklogId()).orElseThrow(() -> new RuntimeException("backlog not found"));
        var sprint1 = Sprint.builder()
                .backlog(backlog)
                .build();
        sprintRepository.save(sprint1);
    }

    public void createTask(CreateTaskDTO task) {
        Backlog backlog = backlogRepository.findById(task.getBacklogId()).orElse(null);
        Sprint sprint = sprintRepository.findById(task.getSprintId()).orElse(null);
        boolean a = sprint != null;
        boolean b = backlog != null;
        if(a == b) {
            throw new RuntimeException("task can only be linked to either sprint or backlog ");
        }
        var task1 = Task.builder()
                .title(task.getTitle())
                .label(Label.valueOf(task.getLabel()))
                .backlog(backlog)
                .sprint(sprint)
                .build();
        taskRepository.save(task1);
    }
}
