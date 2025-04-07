package com.midou.tutorial.Projects.controller;

import com.midou.tutorial.Projects.DTO.ProjectCreateResponse;
import com.midou.tutorial.Projects.DTO.ProjectDetailsResponse;
import com.midou.tutorial.Projects.entities.Project;
import com.midou.tutorial.Projects.enums.Visibility;
import com.midou.tutorial.Projects.services.ProjectService;
import com.midou.tutorial.Projects.DTO.ProjectRequest;
import com.midou.tutorial.Projects.DTO.CardRequest;
import com.midou.tutorial.user.entities.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "${frontend.url}")
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

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

}