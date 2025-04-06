package com.midou.tutorial.backlog.services;

import com.midou.tutorial.Projects.entities.Project;
import com.midou.tutorial.Projects.entities.ProjectCard;
import com.midou.tutorial.Projects.repositories.ProjectCardRepository;
import com.midou.tutorial.Projects.repositories.ProjectRepository;
import com.midou.tutorial.backlog.dto.sprintDTO.CreateSprintDTO;
import com.midou.tutorial.backlog.dto.sprintDTO.SprintResponseDTO;
import com.midou.tutorial.backlog.dto.sprintDTO.UpdateSprintTitleDTO;
import com.midou.tutorial.backlog.dto.taskDTO.GetBacklogTasksDTO;
import com.midou.tutorial.backlog.dto.taskDTO.GetSprintTasksDTO;
import com.midou.tutorial.backlog.entities.Backlog;
import com.midou.tutorial.backlog.entities.Sprint;
import com.midou.tutorial.backlog.entities.task.Task;
import com.midou.tutorial.backlog.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    private final ProjectCardRepository projectCardRepository;
    private final TaskRepository taskRepository;

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
                .started(false)
                .completed(false)
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
        if(sprint.getStarted()){
            throw new RuntimeException("Sprint " + sprint.getSprintId() + " is already started , terminate sprint before deleting");
        }
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
                .map(sprint -> new SprintResponseDTO(
                        sprint.getSprintId(),
                        sprint.getTitle(),
                        sprint.getStarted(),
                        sprint.getCompleted()
                ))
                .collect(Collectors.toList());
    }

    public List<GetBacklogTasksDTO> getBacklogTasks(long id) {
        Backlog backlog = backlogRepository.findById(id).orElseThrow(() -> new RuntimeException("backlog not found"));


            return backlog.getTasks().stream()
                    .map(task -> new GetBacklogTasksDTO(
                            task.getTaskId(),
                            task.getTitle(),
                            task.getLabel(),
                            task.getBacklog().getBacklogId()
                    ))
                    .collect(Collectors.toList());

    }

    public List<GetSprintTasksDTO> getSprintTasks(long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId).orElseThrow(() -> new RuntimeException("sprint not found"));


        return sprint.getTasks().stream()
                .map(task -> new GetSprintTasksDTO(
                        task.getTaskId(),
                        task.getTitle(),
                        task.getLabel(),
                        task.getSprint().getSprintId()
                ))
                .collect(Collectors.toList());

    }

    public ResponseEntity<String> startSprint(long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        if (sprint.getStarted()) {
            return ResponseEntity.badRequest().body("Sprint already started");
        }

        if (sprint.getCompleted()) {
            return ResponseEntity.badRequest().body("Sprint already completed");
        }

        Backlog backlog = sprint.getBacklog();

        // Check if another sprint is already started
        boolean anotherSprintRunning = backlog.getSprints().stream()
                .anyMatch(s -> s.getStarted() && s.getSprintId() != sprint.getSprintId());
        if (anotherSprintRunning) {
            return ResponseEntity.badRequest().body("Another sprint is already started. Please complete it before starting a new one.");
        }

        // Create project cards
        Project project = backlog.getProject();
        ProjectCard todoCard = ProjectCard.builder().name("TO DO").project(project).build();
        ProjectCard inProgressCard = ProjectCard.builder().name("IN PROGRESS").project(project).build();
        ProjectCard doneCard = ProjectCard.builder().name("DONE").project(project).build();

        projectCardRepository.save(todoCard);
        projectCardRepository.save(inProgressCard);
        projectCardRepository.save(doneCard);

        for (Task task : sprint.getTasks()) {
            switch (task.getLabel()) {
                case TODO -> todoCard.addTask(task);
                case INPROGRESS -> inProgressCard.addTask(task);
                case DONE -> doneCard.addTask(task);
            }
            taskRepository.save(task); // Save card assignment
        }

        sprint.setStarted(true);
        sprintRepository.save(sprint);

        return ResponseEntity.ok("Sprint started successfully");
    }


    public ResponseEntity<String> terminateSprint(long sprintId) {
        Sprint sprint = sprintRepository.findById(sprintId)
                .orElseThrow(() -> new RuntimeException("Sprint not found"));

        if (!sprint.getStarted()) {
            return ResponseEntity.badRequest().body("Sprint hasn't started yet");
        }

        if (sprint.getCompleted()) {
            return ResponseEntity.badRequest().body("Sprint already completed");
        }

        Project project = sprint.getBacklog().getProject();

        // Unlink tasks from cards
        List<Task> sprintTasks = sprint.getTasks();
        for (Task task : sprintTasks) {
            task.setCard(null);
            taskRepository.save(task);
        }

        // Delete project cards for this sprint
        List<ProjectCard> cards = projectCardRepository.findByProject(project);
        projectCardRepository.deleteAll(cards);

        // Mark sprint as completed
        sprint.setStarted(false);
        sprint.setCompleted(true);
        sprintRepository.save(sprint);

        return ResponseEntity.ok("Sprint terminated successfully");
    }



}
