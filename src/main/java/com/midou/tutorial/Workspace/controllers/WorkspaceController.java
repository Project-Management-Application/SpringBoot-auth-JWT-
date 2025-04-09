package com.midou.tutorial.Workspace.controllers;



import com.midou.tutorial.Workspace.DTO.*;
import com.midou.tutorial.Workspace.entities.Workspace;
import com.midou.tutorial.Workspace.services.WorkspaceService;
import com.midou.tutorial.user.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import com.midou.tutorial.user.entities.User;

import java.util.List;

@RestController
@CrossOrigin(origins = "${frontend.url}")
@RequestMapping("api/v1/workspace")
@RequiredArgsConstructor
public class WorkspaceController {

    @Autowired
    private WorkspaceService workspaceService;

    @PostMapping("/createWorkspace")
    public ResponseEntity<WorkspaceDTO> createWorkspace(@RequestBody WorkspaceRequest request) {
        // Ensure the user is authenticated
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Workspace workspace = workspaceService.createWorkspace(request.getName());

        // Map to DTO
        WorkspaceDTO dto = new WorkspaceDTO();
        dto.setId(workspace.getId());
        dto.setName(workspace.getName());

        UserDTO userDTO = new UserDTO();
        userDTO.setId(workspace.getOwner().getId());
        userDTO.setFirstName(workspace.getOwner().getFirstName());
        userDTO.setLastName(workspace.getOwner().getLastName());
        userDTO.setEmail(workspace.getOwner().getEmail());


        return ResponseEntity.ok(dto);
    }

    @GetMapping("/my-workspace")
    public ResponseEntity<?> getMyWorkspace() {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            DashboardDTO dashboard = workspaceService.getDashboardData(user.getId());
            return ResponseEntity.ok(dashboard);
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to retrieve workspace: " + e.getMessage());
        }
    }

    @PostMapping("/{workspaceId}/invite")
    public ResponseEntity<?> inviteUser(
            @PathVariable Long workspaceId,
            @RequestBody InviteRequest request) {
        try {
            if (request.getEmail() == null || request.getEmail().isEmpty()) {
                return ResponseEntity.badRequest().body("Email is required");
            }

            User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            workspaceService.inviteUser(workspaceId, request.getEmail(), owner);
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
    public ResponseEntity<List<WorkspaceInvitationProjection>> getPendingInvitations() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        System.out.println("Fetching pending invitations for user: " + user.getEmail());
        List<WorkspaceInvitationProjection> invitations = workspaceService.getPendingInvitations(user);
        System.out.println("Returning " + invitations.size() + " invitations");
        return ResponseEntity.ok(invitations);
    }

    @PostMapping("/invitations/accept/{invitationId}")
    public ResponseEntity<WorkspaceDTO> acceptInvitation(@PathVariable Long invitationId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        WorkspaceDTO workspaceDTO = workspaceService.acceptInvitation(invitationId, user);
        return ResponseEntity.ok(workspaceDTO);
    }

    @PostMapping("/invitations/reject/{invitationId}")
    public ResponseEntity<Void> rejectInvitation(@PathVariable Long invitationId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        workspaceService.rejectInvitation(invitationId, user);
        return ResponseEntity.ok().build();
    }
    @GetMapping("/{workspaceId}/members")
    public ResponseEntity<?> getWorkspaceMembers(@PathVariable Long workspaceId) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            List<MemberDTO> members = workspaceService.getWorkspaceMembers(workspaceId, currentUser);
            return ResponseEntity.ok(members);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error fetching workspace members: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to fetch workspace members: " + e.getMessage());
        }
    }

    @DeleteMapping("/{workspaceId}/members/{memberId}")
    public ResponseEntity<?> removeMemberFromWorkspace(
            @PathVariable Long workspaceId,
            @PathVariable Long memberId) {
        try {
            User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            workspaceService.removeMemberFromWorkspace(workspaceId, memberId, currentUser);
            return ResponseEntity.ok("Member removed successfully and notified via email");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            System.err.println("Error removing member: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to remove member: " + e.getMessage());
        }
    }

}