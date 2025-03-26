package com.midou.tutorial.backlog.services;

import com.midou.tutorial.backlog.dto.sprintDTO.CreateSprintDTO;
import com.midou.tutorial.backlog.dto.sprintDTO.UpdateSprintTitleDTO;
import com.midou.tutorial.backlog.entities.Backlog;
import com.midou.tutorial.backlog.entities.Sprint;
import com.midou.tutorial.backlog.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BacklogService {

    private final BacklogRepository backlogRepository;
    private final SprintRepository sprintRepository;

    public long createBacklog() {
        var backlog = Backlog.builder()
                .build();
        backlogRepository.save(backlog);
        return backlog.getBacklogId();
    }

    public long createSprint(CreateSprintDTO sprint) {
        Backlog backlog = backlogRepository.findById(sprint.getBacklogId()).orElseThrow(() -> new RuntimeException("backlog not found"));
        var sprint1 = Sprint.builder()
                .backlog(backlog)
                .build();
        sprintRepository.save(sprint1);
        return sprint1.getSprintId();
    }

    public long updateSprintTitle(UpdateSprintTitleDTO sprint) {
        Sprint sprint1 = sprintRepository.findById(sprint.getSprintId()).orElseThrow(() -> new RuntimeException("sprint not found"));
        sprint1.setTitle(sprint.getTitle());
        return sprintRepository.save(sprint1).getSprintId();
    }

    public long deleteSprint(long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId).orElseThrow(() -> new RuntimeException("sprint not found"));
        sprintRepository.delete(sprint);
        return sprint.getSprintId();
    }





}
