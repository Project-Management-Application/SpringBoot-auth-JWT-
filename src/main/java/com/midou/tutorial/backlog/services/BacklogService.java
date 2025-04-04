package com.midou.tutorial.backlog.services;

import com.midou.tutorial.Projects.entities.Project;
import com.midou.tutorial.Projects.repositories.ProjectRepository;
import com.midou.tutorial.backlog.dto.sprintDTO.CreateSprintDTO;
import com.midou.tutorial.backlog.dto.sprintDTO.SprintResponseDTO;
import com.midou.tutorial.backlog.dto.sprintDTO.UpdateSprintTitleDTO;
import com.midou.tutorial.backlog.dto.taskDTO.TaskResponseDTO;
import com.midou.tutorial.backlog.entities.Backlog;
import com.midou.tutorial.backlog.entities.Sprint;
import com.midou.tutorial.backlog.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BacklogService {

    private final BacklogRepository backlogRepository;
    private final SprintRepository sprintRepository;
    private final ProjectRepository projectRepository;

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
        sprint1.setTitle("Sprint " + sprint1.getSprintId());
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


    public long getBacklog(long projectId) {
        Project project= projectRepository.findById(projectId).orElseThrow(() -> new RuntimeException("project not found"));
        if (project.getBacklog() == null) {
            throw new RuntimeException("backlog not found");
        }
        return project.getBacklog().getBacklogId();
    }

    public List<SprintResponseDTO> getSprints(long backlogId) {
        Backlog backlog = backlogRepository.findById(backlogId).orElseThrow(() -> new RuntimeException("backlog not found"));

        return backlog.getSprints().stream()
                .map(sprint -> new SprintResponseDTO(sprint.getSprintId(), sprint.getTitle()))
                .collect(Collectors.toList());
    }

    public List<TaskResponseDTO> getTasks(long id) {
        Backlog backlog = backlogRepository.findById(id).orElse(null);
        Sprint sprint = sprintRepository.findById(id).orElse(null);

        if (backlog != null) {
            return backlog.getTasks().stream()
                    .map(task -> new TaskResponseDTO(
                            task.getTaskId(),
                            task.getTitle(),
                            task.getLabel(),
                            task.getBacklog() != null ? task.getBacklog().getBacklogId() : 0,
                            task.getSprint() != null ? task.getSprint().getSprintId() : 0
                    ))
                    .collect(Collectors.toList());
        } else if (sprint != null) {
            return sprint.getTasks().stream()
                    .map(task -> new TaskResponseDTO(
                            task.getTaskId(),
                            task.getTitle(),
                            task.getLabel(),
                            task.getBacklog() != null ? task.getBacklog().getBacklogId() : 0,
                            task.getSprint() != null ? task.getSprint().getSprintId() : 0
                    ))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
