package com.midou.tutorial.backlog.controllers;

import com.midou.tutorial.backlog.dto.sprintDTO.CreateSprintDTO;
import com.midou.tutorial.backlog.dto.sprintDTO.SprintResponseDTO;
import com.midou.tutorial.backlog.dto.sprintDTO.UpdateSprintTitleDTO;
import com.midou.tutorial.backlog.dto.taskDTO.GetBacklogTasksDTO;
import com.midou.tutorial.backlog.dto.taskDTO.GetSprintTasksDTO;
import com.midou.tutorial.backlog.services.BacklogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class BacklogController {
    private final BacklogService backlogService;

    @PostMapping("/createBacklog")
    public long createBacklog() {
        return backlogService.createBacklog();
    }

    @PostMapping("/createSprint")
    public long createSprint(@RequestBody CreateSprintDTO sprint) {
        return backlogService.createSprint(sprint);
    }

    @PatchMapping("/updateSprintTitle")
    public long updateSprintTitle(@RequestBody UpdateSprintTitleDTO sprint){
        return backlogService.updateSprintTitle(sprint);
    }

    @DeleteMapping("/deleteSprint/{sprintId}")
    public long deleteSprint(@PathVariable long sprintId){
        return backlogService.deleteSprint(sprintId);
    }

    @GetMapping("/getBacklog/{projectId}")
    public long getBacklog(@PathVariable long projectId){
        return backlogService.getBacklog(projectId);
    }

    @GetMapping("/getSprints/{backlogId}")
    public List<SprintResponseDTO> getSprints(@PathVariable long backlogId){
        return backlogService.getSprints(backlogId);
    }

    @GetMapping("/getBacklogTasks/{backlogId}")
    public List<GetBacklogTasksDTO> getBacklogTasks(@PathVariable long backlogId){
        return backlogService.getTasks(backlogId);
    }

    @GetMapping("/getSprintTasks/{sprintId}")
    public List<GetSprintTasksDTO> getSprintTasks(@PathVariable long sprintId){
        return backlogService.getSprintTasks(sprintId);
    }

    @PostMapping("startSprint/{sprintId}")
    public ResponseEntity<String> startSprint(@PathVariable long sprintId){
        return backlogService.startSprint(sprintId);
    }

    @PostMapping("/terminateSprint/{sprintId}")
    public ResponseEntity<String> terminateSprint(@PathVariable long sprintId) {
        return backlogService.terminateSprint(sprintId);
    }





















}
