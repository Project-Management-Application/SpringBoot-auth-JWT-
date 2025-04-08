package com.midou.tutorial.Workspace.services;

import com.midou.tutorial.Projects.entities.Project;
import com.midou.tutorial.Workspace.DTO.DashboardDTO;
import com.midou.tutorial.Workspace.DTO.MemberDTO;
import com.midou.tutorial.Workspace.DTO.ProjectDTO;
import com.midou.tutorial.Workspace.DTO.WorkspaceDTO;
import com.midou.tutorial.Workspace.entities.Workspace;
import com.midou.tutorial.Workspace.entities.WorkspaceMember;
import com.midou.tutorial.Workspace.entities.WorkspaceInvitation;
import com.midou.tutorial.Workspace.repositories.WorkspaceRepository;
import com.midou.tutorial.Workspace.repositories.WorkspaceMemberRepository;
import com.midou.tutorial.Workspace.repositories.WorkspaceInvitationRepository;
import com.midou.tutorial.user.entities.User;
import com.midou.tutorial.user.repositories.UserRepository;
import com.midou.tutorial.user.services.EmailService;
import jakarta.persistence.Column;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
        System.out.println("WorkspaceService: Inviting user to workspace ID: " + workspaceId);
        System.out.println("Authenticated user ID: " + owner.getId() + ", Email: " + owner.getEmail());

        Workspace workspace = workspaceRepository.findById(workspaceId)
                .orElseThrow(() -> new IllegalArgumentException("Workspace not found"));
        System.out.println("Workspace found: ID: " + workspace.getId() + ", Owner ID: " + workspace.getOwner().getId());

        // Check if the authenticated user is the workspace owner
        if (workspace.getOwner().getId() != owner.getId()) { // Use == for primitive long comparison
            System.out.println("Access denied: Authenticated user is not the workspace owner");
            throw new IllegalStateException("Only the workspace owner can invite users.");
        }

        // Check if the email belongs to the owner (prevent self-invitation)
        if (email.equals(owner.getEmail())) {
            System.out.println("Cannot invite yourself to the workspace");
            throw new IllegalArgumentException("You cannot invite yourself to your own workspace.");
        }

        User invitedUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        System.out.println("Invited user found: ID: " + invitedUser.getId() + ", Email: " + invitedUser.getEmail());

        // Check if the invited user is already a member of the workspace
        boolean isAlreadyMember = workspace.getMembers().stream()
                .anyMatch(member -> member.getUser().getId() == invitedUser.getId()); // Use == for primitive long comparison
        if (isAlreadyMember) {
            System.out.println("User is already a member of the workspace");
            throw new IllegalArgumentException("This user is already a member of the workspace.");
        }

        WorkspaceInvitation invitation = WorkspaceInvitation.builder()
                .workspace(workspace)
                .invitedUser(invitedUser)
                .expiresAt(LocalDateTime.now().plusDays(7))
                .build();
        invitationRepository.save(invitation);
        System.out.println("Invitation saved: ID: " + invitation.getId());

        String subject = "Workspace Invitation";
        String body = "Hello " + invitedUser.getFirstName() + invitedUser.getLastName() + ",\n" +
                "You’ve been invited to join the workspace '" + workspace.getName() + "'.\n" +
                "To accept, use this link: http://localhost:8090/api/v1/workspace/invitations/accept/" + invitation.getId() + "\n" +
                "This invitation expires on " + invitation.getExpiresAt() + ".\n" +
                "Please log in to accept the invitation.";
        emailService.sendMail(email, subject, body);
        System.out.println("Invitation email sent to: " + email);
    }

    @Transactional
    public void acceptInvitation(Long invitationId, User user) {
        WorkspaceInvitation invitation = invitationRepository.findById(invitationId)
                .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));
        if (invitation.getInvitedUser().getId() != user.getId()) {
            throw new IllegalStateException("You can only accept your own invitations.");
        }

        WorkspaceMember member = WorkspaceMember.builder()
                .workspace(invitation.getWorkspace())
                .user(user)
                .build();
        workspaceMemberRepository.save(member);
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

}