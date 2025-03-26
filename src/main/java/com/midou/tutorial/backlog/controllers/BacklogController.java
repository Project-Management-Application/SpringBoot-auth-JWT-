package com.midou.tutorial.backlog.controllers;

import com.midou.tutorial.backlog.dto.sprintDTO.CreateSprintDTO;
import com.midou.tutorial.backlog.dto.sprintDTO.UpdateSprintTitleDTO;
import com.midou.tutorial.backlog.services.BacklogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


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

    @DeleteMapping("/deleteSprint")
    public long deleteSprint(@RequestBody long sprintId){
        return backlogService.deleteSprint(sprintId);
    }























}
