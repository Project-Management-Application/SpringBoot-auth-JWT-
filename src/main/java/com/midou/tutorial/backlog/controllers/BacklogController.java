package com.midou.tutorial.backlog.controllers;

import com.midou.tutorial.backlog.dto.CreateSprintDTO;
import com.midou.tutorial.backlog.dto.CreateTaskDTO;
import com.midou.tutorial.backlog.services.BacklogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BacklogController {
    private final BacklogService backlogService;

    @PostMapping("/backlog")
    public void createBacklog() {
        backlogService.createBacklog();
    }
    @PostMapping("/sprint")
    public void createSprint(@RequestBody CreateSprintDTO sprint) {
        backlogService.createSprint(sprint);
    }
    @PostMapping("/task")
    public void createTask(@RequestBody CreateTaskDTO task) {
        backlogService.createTask(task);
    }
}
