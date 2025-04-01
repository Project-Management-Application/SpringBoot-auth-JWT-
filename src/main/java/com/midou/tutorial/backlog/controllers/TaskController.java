package com.midou.tutorial.backlog.controllers;

import com.midou.tutorial.backlog.dto.taskDTO.*;
import com.midou.tutorial.backlog.services.TaskService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("/createTask")
    public long createTask(@RequestBody CreateTaskDTO task) {
        return taskService.createTask(task);
    }

    @PatchMapping("/updateTaskTitle")
    public long updateTaskTitle(@RequestBody UpdateTaskTitleDTO task){
        return taskService.updateTaskTitle(task);
    }

    @PatchMapping("/updateTaskLabel")
    public long updateTaskLabel(@RequestBody UpdateTaskLabelDTO task){
        return taskService.updateTaskLabel(task);
    }

    @PatchMapping("/updateTaskDescription")
    public long updateTaskDescription(@RequestBody UpdateTaskDescriptionDTO task){
        return taskService.updateTaskDescription(task);
    }

    @DeleteMapping("/deleteTask/{taskId}")
    public long deleteTask(@PathVariable long taskId){
        return taskService.deleteTask(taskId);
    }

    @PostMapping("/addTicketToTask")
    public long addTicketToTask(@RequestBody TicketTaskDTO taskTicket){
        return taskService.addTicketToTask(taskTicket);
    }

    @DeleteMapping("/removeTicketFromTask")
    public long removeTicketFromTask(@RequestBody TicketTaskDTO taskTicket){
        return taskService.removeTicketFromTask(taskTicket);
    }
}
