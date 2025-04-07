package com.midou.tutorial.Projects.services;

import com.midou.tutorial.Models.entities.Model;
import com.midou.tutorial.Models.entities.ModelCard;
import com.midou.tutorial.Models.repositories.ModelRepository;
import com.midou.tutorial.Projects.DTO.ProjectDetailsResponse;
import com.midou.tutorial.Projects.entities.Project;
import com.midou.tutorial.Projects.entities.ProjectCard;
import com.midou.tutorial.Projects.enums.ProjectRole;
import com.midou.tutorial.Projects.enums.Visibility;
import com.midou.tutorial.Projects.repositories.ProjectRepository;
import com.midou.tutorial.Workspace.entities.Workspace;
import com.midou.tutorial.Workspace.repositories.WorkspaceRepository;
import com.midou.tutorial.backlog.entities.Backlog;
import com.midou.tutorial.backlog.repositories.BacklogRepository;
import com.midou.tutorial.user.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private WorkspaceRepository workspaceRepository;
    @Autowired
    private BacklogRepository backlogRepository;

    @Transactional
    public Project createProject(String name, String description, Visibility visibility, Long modelId, Long workspaceId, String backgroundImage, String backgroundColor, User owner) {
        // Fetch the workspace
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));

        if (workspace.getOwner().getId() != owner.getId()) {
            throw new IllegalStateException("Only the workspace owner can create projects in this workspace.");
        }

        // If a modelId is provided, ignore backgroundImage and backgroundColor
        if (modelId != null) {
            backgroundImage = null;
            backgroundColor = null;
        } else {
            // If no modelId is provided, ensure at least one of backgroundImage or backgroundColor is set
            if (backgroundImage == null && backgroundColor == null) {
                throw new IllegalArgumentException("Either a background image or a background color must be provided when no model is selected.");
            }
            // Optional: Ensure only one of backgroundImage or backgroundColor is set (if you want to enforce this)
            if (backgroundImage != null && backgroundColor != null) {
                throw new IllegalArgumentException("Cannot set both a background image and a background color. Choose one.");
            }
        }

        // Create the project
        Project project = Project.builder()
                .name(name)
                .description(description)
                .visibility(visibility)
                .workspace(workspace)
                .owner(owner)
                .backgroundImage(backgroundImage)
                .backgroundColor(backgroundColor)
                .build();

        // If a modelId is provided, copy the model's cards
        if (modelId != null) {
            Model model = modelRepository.findById(modelId)
                    .orElseThrow(() -> new IllegalArgumentException("Model not found"));
            project.setModel(model);
            if (Objects.equals(model.getName(), "Scrum Agile")){
                var backlog1 = Backlog.builder()
                        .project(project)
                        .build();
                backlogRepository.save(backlog1);
                project.setBacklog(backlog1);
            }


            // Copy ModelCard entities into ProjectCard entities
            List<ProjectCard> copiedCards = model.getCards().stream()
                    .map(modelCard -> ProjectCard.builder()
                            .name(modelCard.getName())
                            .project(project)
                            .build())
                    .collect(Collectors.toList());
            project.setCards(copiedCards);
        }

        // Save the project (cards will be saved due to cascade)
        Project savedProject = projectRepository.save(project);
        System.out.println("Project created with ID: " + savedProject.getId());
        return savedProject;
    }

    @Transactional
    public void addCardToProject(Long projectId, String cardName, User currentUser) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        boolean isEditor = project.getMembers().stream()
                .anyMatch(member -> member.getId() == currentUser.getId()
                        && member.getRole().name().equals(ProjectRole.EDITOR.name()));

        if (!isEditor && project.getOwner().getId() != currentUser.getId()) {
            throw new IllegalStateException("Only editors or the owner can add cards to this project.");
        }

        ProjectCard card = ProjectCard.builder()
                .name(cardName)
                .project(project)
                .build();

        project.getCards().add(card);
        projectRepository.save(project);
        System.out.println("Card '" + cardName + "' added to project ID: " + projectId);
    }


    public Project getProjectById(Long projectId) {
        return projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));
    }

    @Transactional(readOnly = true)
    public ProjectDetailsResponse getProjectDetails(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        ProjectDetailsResponse response = new ProjectDetailsResponse();
        response.setId(project.getId());
        response.setName(project.getName());
        response.setDescription(project.getDescription());
        response.setVisibility(project.getVisibility());
        response.setBackgroundImage(project.getBackgroundImage());
        response.setBackgroundColor(project.getBackgroundColor());
        response.setWorkspaceId(project.getWorkspace().getId());
        response.setOwnerId(project.getOwner().getId());
        response.setModelId(project.getModel() != null ? project.getModel().getId() : null);
        response.setModelBackgroundImage(project.getModel() != null ? project.getModel().getBackgroundImage() : null); // Set model's background image

        List<ProjectDetailsResponse.ProjectCardDTO> cardDTOs = project.getCards().stream()
                .map(card -> {
                    ProjectDetailsResponse.ProjectCardDTO dto = new ProjectDetailsResponse.ProjectCardDTO();
                    dto.setId(card.getId());
                    dto.setName(card.getName());
                    return dto;
                })
                .collect(Collectors.toList());
        response.setCards(cardDTOs);

        List<ProjectDetailsResponse.ProjectMemberDTO> memberDTOs = project.getMembers().stream()
                .map(member -> {
                    ProjectDetailsResponse.ProjectMemberDTO dto = new ProjectDetailsResponse.ProjectMemberDTO();
                    dto.setUserId(member.getId());
                    dto.setRole(ProjectRole.valueOf(member.getRole().name()));
                    return dto;
                })
                .collect(Collectors.toList());
        response.setMembers(memberDTOs);

        return response;
    }

}