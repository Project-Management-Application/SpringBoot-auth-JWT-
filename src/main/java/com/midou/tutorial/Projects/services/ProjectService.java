package com.midou.tutorial.Projects.services;

import com.midou.tutorial.Models.entities.Model;
import com.midou.tutorial.Models.entities.ModelCard;
import com.midou.tutorial.Models.repositories.ModelRepository;
import com.midou.tutorial.Projects.DTO.*;
import com.midou.tutorial.Projects.entities.*;
import com.midou.tutorial.Projects.enums.ProjectRole;
import com.midou.tutorial.Projects.enums.Visibility;
import com.midou.tutorial.Projects.repositories.*;
import com.midou.tutorial.Workspace.entities.Workspace;
import com.midou.tutorial.Workspace.repositories.WorkspaceRepository;
import com.midou.tutorial.backlog.entities.Backlog;
import com.midou.tutorial.backlog.entities.task.Task;
import com.midou.tutorial.backlog.repositories.BacklogRepository;
import com.midou.tutorial.backlog.repositories.TaskRepository;
import com.midou.tutorial.user.entities.User;
import com.midou.tutorial.user.repositories.UserRepository;
import com.midou.tutorial.user.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.midou.tutorial.Projects.repositories.ProjectRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @Autowired
    private ProjectInvitationRepository projectInvitationRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private ProjectCardRepository projectCardRepository;
    @Autowired
    private ProjectTaskService projectTaskService;
    @Autowired
    private ProjectTaskRepository projectTaskRepository;

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
                .anyMatch(member -> member.getUser().getId() == currentUser.getId()
                        && member.getRole().name().contentEquals(ProjectRole.EDITOR.name()));

        if (!isEditor && project.getOwner().getId() != currentUser.getId()) {
            throw new IllegalStateException("Only editors or the owner can add cards to this project.");
        }

        // Check for duplicate manually (without equals)
        boolean cardExists = project.getCards().stream()
                .anyMatch(card -> {
                    String existingName = card.getName();
                    if (existingName == null || cardName == null) {
                        return false;
                    }
                    return existingName.length() == cardName.length()
                            && existingName.compareToIgnoreCase(cardName) == 0;
                });

        if (cardExists) {
            throw new IllegalStateException("A card with the same name already exists in this project.");
        }

        ProjectCard card = ProjectCard.builder()
                .name(cardName)
                .project(project)
                .build();

        project.getCards().add(card);
        projectRepository.save(project);
        System.out.println("Card '" + cardName + "' added to project ID: " + projectId);
    }



    public void addTaskToCard(Long cardId, Long taskId) {
        ProjectCard card = projectCardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        card.getTasks().add(task);
        task.setCard(card);
        projectCardRepository.save(card);
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
        response.setVisibility(String.valueOf(project.getVisibility()));
        response.setBackgroundImage(project.getBackgroundImage());
        response.setBackgroundColor(project.getBackgroundColor());
        response.setWorkspaceId(project.getWorkspace().getId());
        response.setOwnerId(project.getOwner().getId());
        response.setModelId(project.getModel() != null ? project.getModel().getId() : null);
        response.setModelBackgroundImage(project.getModel() != null ? project.getModel().getBackgroundImage() : null);

        List<ProjectDetailsResponse.ProjectCardDTO> cardDTOs = project.getCards().stream()
                .map(card -> {
                    ProjectDetailsResponse.ProjectCardDTO dto = new ProjectDetailsResponse.ProjectCardDTO();
                    dto.setId(card.getId());
                    dto.setName(card.getName());
                    // Fetch tasks for this card
                    List<ProjectTaskSummaryDTO> tasks = projectTaskService.getTasksByCardId(card.getId());
                    dto.setTasks(tasks);
                    return dto;
                })
                .collect(Collectors.toList());
        response.setCards(cardDTOs);

        List<ProjectDetailsResponse.ProjectMemberDTO> memberDTOs = project.getMembers().stream()
                .map(member -> {
                    ProjectDetailsResponse.ProjectMemberDTO dto = new ProjectDetailsResponse.ProjectMemberDTO();
                    dto.setUserId(member.getUser().getId());
                    dto.setRole(ProjectRole.valueOf(member.getRole().name()));
                    return dto;
                })
                .collect(Collectors.toList());
        response.setMembers(memberDTOs);

        return response;
    }


    @Transactional
    public void inviteUser(Long projectId, String email, String role, User owner) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        if (project.getOwner().getId() != owner.getId()) {
            throw new IllegalStateException("Only the project owner can invite users.");
        }

        if (email.equals(owner.getEmail())) {
            throw new IllegalArgumentException("You cannot invite yourself.");
        }

        User invitedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (project.getMembers().stream().anyMatch(m -> m.getUser().getId() == invitedUser.getId())) {
            throw new IllegalArgumentException("User is already a member of this project.");
        }



        ProjectRole projectRole;
        try {
            projectRole = ProjectRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid role specified. Use 'VIEWER' or 'EDITOR'.");
        }

        ProjectInvitation invitation = ProjectInvitation.builder()
                .project(project)
                .invitedUser(invitedUser)
                .role(projectRole)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        projectInvitationRepository.save(invitation);

        String subject = "Project Invitation";
        String body = "Hello " + invitedUser.getFirstName() + ",\n" +
                "You’ve been invited to join the project '" + project.getName() + "' as a " + projectRole + ".\n" +
                "Please log in to Taskify to accept or reject this invitation.\n" +
                "This invitation expires on " + invitation.getExpiresAt() + ".";
        emailService.sendMail(email, subject, body);
    }

    @Transactional(readOnly = true)
    public List<ProjectInvitationProjection> getPendingInvitations(User user) {
        return projectInvitationRepository.findPendingInvitationsByUser(user);
    }

    @Transactional
    public ProjectDTO acceptInvitation(Long invitationId, User userFromSecurity) {
        ProjectInvitation invitation = projectInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        User user = userRepository.findById(userFromSecurity.getId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        // Use '==' for primitive comparison
        if (invitation.getInvitedUser().getId() != user.getId()) {
            throw new IllegalStateException("You can only accept your own invitations.");
        }

        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invitation has expired.");
        }

        Project project = invitation.getProject();

        ProjectMember member = new ProjectMember();
        member.setProject(project);
        member.setUser(user);
        member.setRole(invitation.getRole());

        projectMemberRepository.save(member);
        projectInvitationRepository.delete(invitation);

        return new ProjectDTO(project.getId(), project.getName());
    }


    @Transactional
    public void rejectInvitation(Long invitationId, User user) {
        ProjectInvitation invitation = projectInvitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

        if (invitation.getInvitedUser().getId() != user.getId()) {
            throw new IllegalStateException("You can only reject your own invitations.");
        }

        projectInvitationRepository.delete(invitation);
    }


    @Transactional
    public TaskCreationResponseDTO moveTask(Long taskId, MoveTaskRequestDTO moveTaskDTO) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + taskId + " not found"));
        ProjectCard targetCard = projectCardRepository.findById(moveTaskDTO.getCardId())
                .orElseThrow(() -> new IllegalArgumentException("Target ProjectCard with ID " + moveTaskDTO.getCardId() + " not found"));
        task.setCard(targetCard);
        ProjectTask updatedTask = projectTaskRepository.save(task);
        return TaskCreationResponseDTO.builder()
                .id(updatedTask.getId())
                .message("Task moved successfully")
                .build();
    }

    @Transactional
    public CardOperationResponseDTO deleteCard(Long cardId) {
        ProjectCard card = projectCardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("Card not found"));

        projectCardRepository.delete(card);

        return CardOperationResponseDTO.builder()
                .cardId(cardId)
                .message("Card deleted successfully")
                .build();
    }


    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("User not authenticated");
        }
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }
    @Transactional(readOnly = true)
    public UserInfoResponseDTO getUserInfo() {
        User user = getCurrentUser();

        return UserInfoResponseDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .build();
    }
    @Transactional(readOnly = true)
    public List<ProjectMemberDTO> getProjectMembers(Long projectId) {
        projectRepository.findById(projectId)
                .orElseThrow(() -> new IllegalArgumentException("Project not found"));

        List<ProjectMember> members = projectMemberRepository.findByProjectId(projectId);

        return members.stream()
                .map(member -> ProjectMemberDTO.builder()
                        .userId(member.getUser().getId())
                        .email(member.getUser().getEmail())
                        .firstName(member.getUser().getFirstName())
                        .lastName(member.getUser().getLastName())
                        .build())
                .collect(Collectors.toList());
    }


}


