package com.midou.tutorial.Projects.controller;

import com.midou.tutorial.Projects.DTO.*;
import com.midou.tutorial.Projects.entities.Project;
import com.midou.tutorial.Projects.DTO.ProjectDTO;
import com.midou.tutorial.Projects.services.ProjectService;
import com.midou.tutorial.Projects.services.ProjectTaskService;
import com.midou.tutorial.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "${frontend.url}")
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final ProjectTaskService projectTaskService;

    @PostMapping("/createProject")
    public ResponseEntity<?> createProject(@RequestBody ProjectRequest request) {
        try {
            User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

            // Validate request
            if (request.getName() == null || request.getName().isEmpty()) {
                return ResponseEntity.badRequest().body("Project name is required");
            }
            if (request.getVisibility() == null) {
                return ResponseEntity.badRequest().body("Visibility is required");
            }
            if (request.getWorkspaceId() == null) {
                return ResponseEntity.badRequest().body("Workspace ID is required");
            }

            Project project = projectService.createProject(
                    request.getName(),
                    request.getDescription(),
                    request.getVisibility(),
                    request.getModelId(),
                    request.getWorkspaceId(),
                    request.getBackgroundImage(),
                    request.getBackgroundColor(),
                    owner
            );

            ProjectCreateResponse response = new ProjectCreateResponse();
            response.setMessage("Project created successfully");
            response.setId(project.getId());
            response.setName(project.getName());

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error creating project: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to create project: " + e.getMessage());
        }
    }

    @PostMapping("/{projectId}/addCard")
    public ResponseEntity<?> addCardToProject(
            @PathVariable Long projectId,
            @RequestBody CardRequest request) {
        try {
            if (request.getName() == null || request.getName().isEmpty()) {
                return ResponseEntity.badRequest().body("Card name is required");
            }

            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            projectService.addCardToProject(projectId, request.getName(), currentUser);
            return ResponseEntity.ok("Card added successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error adding card to project: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to add card to project: " + e.getMessage());
        }
    }



    @GetMapping("FetchProject/{projectId}")
    public ResponseEntity<?> getProjectDetails(@PathVariable Long projectId) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            ProjectDetailsResponse projectDetails = projectService.getProjectDetails(projectId);
            if (!projectDetails.getOwnerId().equals(currentUser.getId())) {
                // Add additional checks for members if needed
                return ResponseEntity.status(403).body("You don't have permission to view this project");
            }
            return ResponseEntity.ok(projectDetails);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error fetching project details: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to fetch project details: " + e.getMessage());
        }
    }

    @PostMapping("/{projectId}/invite")
    public ResponseEntity<?> inviteUser(
            @PathVariable Long projectId,
            @RequestBody InviteRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }
            if (request.getRole() == null) {
                return ResponseEntity.badRequest().body("Role is required");
            }

            User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            projectService.inviteUser(projectId, request.getEmail(), request.getRole(), owner);
            return ResponseEntity.ok("Invitation sent successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error inviting user: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to invite user: " + e.getMessage());
        }
    }

    @GetMapping("/invitations/pending")
    public ResponseEntity<List<ProjectInvitationProjection>> getPendingInvitations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Fetching pending project invitations for user: " + user.getEmail());
        List<ProjectInvitationProjection> invitations = projectService.getPendingInvitations(user);
        System.out.println("Returning " + invitations.size() + " invitations");
        return ResponseEntity.ok(invitations);
    }

    @PostMapping("/invitations/accept/{invitationId}")
    public ResponseEntity<ProjectDTO> acceptInvitation(@PathVariable Long invitationId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        ProjectDTO projectDTO = projectService.acceptInvitation(invitationId, user);
        return ResponseEntity.ok(projectDTO);
    }

    @PostMapping("/invitations/reject/{invitationId}")
    public ResponseEntity<Void> rejectInvitation(@PathVariable Long invitationId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        projectService.rejectInvitation(invitationId, user);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/deleteCard/{cardId}")
    public ResponseEntity<CardOperationResponseDTO> deleteCard(@PathVariable Long cardId) {
        try {
            CardOperationResponseDTO response = projectService.deleteCard(cardId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error deleting card: " + e.getMessage());
            return ResponseEntity.badRequest().body(CardOperationResponseDTO.builder()
                    .cardId(cardId)
                    .message("Error: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/tasks/deleteTask/{taskId}")
    public ResponseEntity<TaskOperationResponseDTO> deleteTask(@PathVariable Long taskId) {
        try {
            TaskOperationResponseDTO response = projectTaskService.deleteTask(taskId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error deleting task: " + e.getMessage());
            return ResponseEntity.badRequest().body(TaskOperationResponseDTO.builder()
                    .taskId(taskId)
                    .message("Error: " + e.getMessage())
                    .build());
        }
    }

    @PutMapping("/tasks/updateTaskName/{taskId}/name")
    public ResponseEntity<TaskOperationResponseDTO> updateTaskName(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskNameRequestDTO request) {
        try {
            TaskOperationResponseDTO response = projectTaskService.updateTaskName(taskId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error updating task name: " + e.getMessage());
            return ResponseEntity.badRequest().body(TaskOperationResponseDTO.builder()
                    .taskId(taskId)
                    .message("Error: " + e.getMessage())
                    .build());
        }
    }

    @GetMapping("/users/getUserInfo")
    public ResponseEntity<UserInfoResponseDTO> getUserInfo() {
        try {
            UserInfoResponseDTO response = projectService.getUserInfo();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error retrieving user info: " + e.getMessage());
            return ResponseEntity.badRequest().body(UserInfoResponseDTO.builder()
                    .id(null)
                    .email(null)
                    .firstName(null)
                    .lastName(null)
                    .build());
        }
    }

    @GetMapping("/{projectId}/getProjectMembers")
    public ResponseEntity<List<ProjectMemberDTO>> getProjectMembers(@PathVariable Long projectId) {
        try {
            List<ProjectMemberDTO> response = projectService.getProjectMembers(projectId);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error retrieving project members: " + e.getMessage());
            return ResponseEntity.badRequest().body(List.of(ProjectMemberDTO.builder()
                    .userId(null)
                    .email(null)
                    .firstName(null)
                    .lastName("Error: " + e.getMessage())
                    .build()));
        }
    }
}