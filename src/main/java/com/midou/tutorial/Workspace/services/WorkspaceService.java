package com.midou.tutorial.Workspace.services;

import com.midou.tutorial.Projects.entities.Project;
import com.midou.tutorial.Workspace.DTO.*;
import com.midou.tutorial.Workspace.entities.Workspace;
import com.midou.tutorial.Workspace.entities.WorkspaceMember;
import com.midou.tutorial.Workspace.entities.WorkspaceInvitation;
import com.midou.tutorial.Workspace.repositories.WorkspaceRepository;
import com.midou.tutorial.Workspace.repositories.WorkspaceMemberRepository;
import com.midou.tutorial.Workspace.repositories.WorkspaceInvitationRepository;
import com.midou.tutorial.user.entities.User;
import com.midou.tutorial.user.repositories.UserRepository;
import com.midou.tutorial.user.services.EmailService;
import com.midou.tutorial.Projects.enums.Visibility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class WorkspaceService {

    @Autowired
    private WorkspaceRepository workspaceRepository;

    @Autowired
    private WorkspaceMemberRepository workspaceMemberRepository;

    @Autowired
    private WorkspaceInvitationRepository invitationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public Workspace createWorkspace(String name) {
        User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (workspaceRepository.existsByOwnerId(owner.getId())) {
            throw new IllegalStateException("User already has a workspace.");
        }

        Workspace workspace = Workspace.builder()
                .name(name)
                .owner(owner)
                .build();

        workspace = workspaceRepository.save(workspace);

        WorkspaceMember member = WorkspaceMember.builder()
                .workspace(workspace)
                .user(owner)
                .build();
        workspaceMemberRepository.save(member);

        return workspace;
    }

    public Workspace getUserWorkspace(Long userId) {
        return workspaceRepository.findByOwnerId(userId).orElse(null);
    }

    @Transactional
    public void inviteUser(Long workspaceId, String email, User owner) {
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        if (workspace.getOwner().getId() != owner.getId()) {
            throw new IllegalStateException("Only the workspace owner can invite users.");
        }
        if (email.equals(owner.getEmail())) {
            throw new IllegalArgumentException("You cannot invite yourself.");
        }
        User invitedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (workspace.getMembers().stream().anyMatch(m -> m.getUser().getId() == invitedUser.getId())) {
            throw new IllegalArgumentException("User is already a member.");
        }

        WorkspaceInvitation invitation = WorkspaceInvitation.builder()
                .workspace(workspace)
                .invitedUser(invitedUser)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        invitationRepository.save(invitation);

        String subject = "Workspace Invitation";
        String body = "Hello " + invitedUser.getFirstName() + ",\n" +
                "You’ve been invited to join the workspace '" + workspace.getName() + "'.\n" +
                "Please log in to Taskify to accept or reject this invitation.\n" +
                "This invitation expires on " + invitation.getExpiresAt() + ".";
        emailService.sendMail(email, subject, body);
    }

    @Transactional(readOnly = true)
    public List<WorkspaceInvitationProjection> getPendingInvitations(User user) {
        return invitationRepository.findPendingInvitationsByUser(user);
    }

    @Transactional
    public WorkspaceDTO acceptInvitation(Long invitationId, User user) {
        WorkspaceInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        if (invitation.getInvitedUser().getId() != user.getId()) {
            throw new IllegalStateException("You can only accept your own invitations.");
        }
        if (invitation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Invitation has expired.");
        }

        Workspace workspace = invitation.getWorkspace();
        WorkspaceMember member = WorkspaceMember.builder()
                .workspace(workspace)
                .user(user)
                .build();
        workspaceMemberRepository.save(member);
        invitationRepository.delete(invitation);

        return new WorkspaceDTO(workspace.getId(), workspace.getName());
    }

    @Transactional
    public void rejectInvitation(Long invitationId, User user) {
        WorkspaceInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        if (invitation.getInvitedUser().getId() != user.getId()) {
            throw new IllegalStateException("You can only reject your own invitations.");
        }
        invitationRepository.delete(invitation);
    }

    @Transactional(readOnly = true)
    public DashboardDTO getDashboardData(Long userId) {
        // Fetch the workspace with its projects and associated models eagerly
        Workspace workspace = entityManager.createQuery(
                        "SELECT w " +
                                "FROM Workspace w " +
                                "LEFT JOIN FETCH w.members wm " +
                                "LEFT JOIN FETCH wm.user u " +
                                "LEFT JOIN FETCH w.projects p " +
                                "LEFT JOIN FETCH p.model m " +
                                "WHERE w.owner.id = :userId", Workspace.class)
                .setParameter("userId", userId)
                .getSingleResult();

        if (workspace == null) {
            throw new IllegalStateException("Workspace not found for user");
        }

        System.out.println("Found workspace: " + workspace.getId() + " with name: " + workspace.getName());
        System.out.println("Projects in workspace: " + (workspace.getProjects() == null ? "null" : workspace.getProjects().size()));

        if (workspace.getProjects() != null) {
            workspace.getProjects().forEach(p -> {
                System.out.println("Project: ID=" + p.getId() + ", Name=" + p.getName() + ", Visibility=" + p.getVisibility() +
                        ", Model ID=" + (p.getModel() != null ? p.getModel().getId() : "null"));
            });
        }

        WorkspaceDTO workspaceDTO = new WorkspaceDTO(
                workspace.getId(),
                workspace.getName()
        );

        List<MemberDTO> memberDTOs = workspace.getMembers().stream()
                .map(wm -> new MemberDTO(
                        wm.getUser().getId(),
                        wm.getUser().getFirstName(),
                        wm.getUser().getLastName(),
                        wm.getUser().getEmail()
                ))
                .collect(Collectors.toList());

        List<ProjectDTO> projectDTOs = workspace.getProjects().stream()
                .map(p -> {
                    Long projectId = p.getId();
                    String projectName = p.getName();
                    String projectVisibility = String.valueOf(p.getVisibility());
                    String backgroundImage = p.getBackgroundImage();
                    String backgroundColor = p.getBackgroundColor();
                    Long modelId = (p.getModel() != null) ? p.getModel().getId() : null;
                    String modelBackgroundImage = (p.getModel() != null) ? p.getModel().getBackgroundImage() : null;

                    return new ProjectDTO(projectId, projectName, projectVisibility, backgroundImage, backgroundColor, modelId, modelBackgroundImage);
                })
                .collect(Collectors.toList());


        return new DashboardDTO(workspaceDTO, memberDTOs, projectDTOs);
    }

    @Transactional(readOnly = true)
    public List<MemberDTO> getWorkspaceMembers(Long workspaceId, User currentUser) {
        System.out.println("WorkspaceService: Fetching members for workspace ID: " + workspaceId);
        System.out.println("Authenticated user ID: " + currentUser.getId() + ", Email: " + currentUser.getEmail());

        // Fetch the workspace
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        System.out.println("Workspace found: ID: " + workspace.getId() + ", Owner ID: " + workspace.getOwner().getId());

        // Check if the authenticated user is the workspace owner
        if (workspace.getOwner().getId() != currentUser.getId()) {
            System.out.println("Access denied: Authenticated user is not the workspace owner");
            throw new IllegalStateException("Only the workspace owner can view members.");
        }

        // Map members to DTOs, excluding the owner
        List<MemberDTO> memberDTOs = workspace.getMembers().stream()
                .filter(wm -> wm.getUser().getId() != workspace.getOwner().getId()) // Exclude the owner
                .map(wm -> new MemberDTO(
                        wm.getUser().getId(),
                        wm.getUser().getFirstName(),
                        wm.getUser().getLastName(),
                        wm.getUser().getEmail()
                ))
                .collect(Collectors.toList());

        System.out.println("Found " + memberDTOs.size() + " members (excluding owner) in workspace ID: " + workspaceId);
        return memberDTOs;
    }


    @Transactional
    public void removeMemberFromWorkspace(Long workspaceId, Long memberId, User currentUser) {

        // Fetch the workspace
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        // Check if the authenticated user is the workspace owner
        if (workspace.getOwner().getId() != currentUser.getId()) {
            System.out.println("Access denied: Authenticated user is not the workspace owner");
            throw new IllegalStateException("Only the workspace owner can remove members.");
        }

        // Prevent removing the owner
        if (workspace.getOwner().getId() == memberId) {
            System.out.println("Cannot remove the owner from the workspace");
            throw new IllegalArgumentException("The owner cannot be removed from the workspace.");
        }

        // Find the member to remove
        WorkspaceMember memberToRemove = workspace.getMembers().stream()
                .filter(wm -> wm.getUser().getId() == memberId)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Member not found in this workspace"));

        // Remove the member
        workspace.getMembers().remove(memberToRemove);
        workspaceMemberRepository.delete(memberToRemove); // Delete from the repository
        workspaceRepository.save(workspace); // Save the updated workspace

        // Send email notification to the removed member
        String email = memberToRemove.getUser().getEmail();
        String subject = "Removed from Workspace";
        String body = "Hello " + memberToRemove.getUser().getFirstName() + ",\n" +
                "You have been removed from the workspace '" + workspace.getName() + "'.";
        emailService.sendMail(email, subject, body);
    }


    @Transactional(readOnly = true)
    public List<WorkspaceDTO> getUserWorkspaces(User user) {
        System.out.println("WorkspaceService: Fetching workspaces for user ID: " + user.getId());

        // Fetch all WorkspaceMember entries for the user
        List<WorkspaceMember> memberships = workspaceMemberRepository.findByUser(user);

        // Map to WorkspaceDTOs, excluding the workspace where the user is the owner
        List<WorkspaceDTO> workspaceDTOs = memberships.stream()
                .filter(wm -> wm.getWorkspace().getOwner().getId() != user.getId()) // Exclude workspace where user is the owner
                .map(wm -> {
                    Workspace workspace = wm.getWorkspace();
                    return new WorkspaceDTO(workspace.getId(), workspace.getName());
                })
                .collect(Collectors.toList());

        System.out.println("Found " + workspaceDTOs.size() + " joined workspaces for user ID: " + user.getId());
        return workspaceDTOs;
    }

    // Fetch all public projects in a workspace
    @Transactional(readOnly = true)
    public List<ProjectDTO> getPublicProjectsInWorkspace(Long workspaceId, User user) {
        System.out.println("WorkspaceService: Fetching public projects for workspace ID: " + workspaceId);

        // Fetch the workspace
        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));

        // Check if the user is a member of the workspace
        boolean isMember = workspace.getMembers().stream()
                .anyMatch(wm -> wm.getUser().getId() == user.getId());
        if (!isMember) {
            System.out.println("Access denied: User ID " + user.getId() + " is not a member of workspace ID " + workspaceId);
            throw new IllegalStateException("You must be a member of the workspace to view its projects.");
        }

        // Fetch public projects
        List<ProjectDTO> publicProjects = workspace.getProjects().stream()
                .filter(project -> project.getVisibility() == Visibility.PUBLIC) // Fixed with proper import
                .map(project -> {
                    Long projectId = project.getId();
                    String projectName = project.getName();
                    String projectVisibility = String.valueOf(project.getVisibility());
                    String backgroundImage = project.getBackgroundImage();
                    String backgroundColor = project.getBackgroundColor();
                    Long modelId = (project.getModel() != null) ? project.getModel().getId() : null;
                    String modelBackgroundImage = (project.getModel() != null) ? project.getModel().getBackgroundImage() : null;

                    return new ProjectDTO(projectId, projectName, projectVisibility, backgroundImage, backgroundColor, modelId, modelBackgroundImage);
                })
                .collect(Collectors.toList());

        System.out.println("Found " + publicProjects.size() + " public projects in workspace ID: " + workspaceId);
        return publicProjects;
    }
}