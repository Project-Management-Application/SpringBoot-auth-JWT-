package com.midou.tutorial.Workspace.controllers;



import com.midou.tutorial.Workspace.DTO.DashboardDTO;
import com.midou.tutorial.Workspace.DTO.InviteRequest;
import com.midou.tutorial.Workspace.DTO.WorkspaceDTO;
import com.midou.tutorial.Workspace.DTO.WorkspaceRequest;
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
    public ResponseEntity<Void> inviteUser(@PathVariable Long workspaceId, @RequestBody InviteRequest request) {
        System.out.println("Inviting user to workspace: " + workspaceId + ", Email: " + request.getEmail());
        try {
            User owner = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            workspaceService.inviteUser(workspaceId, request.getEmail(), owner);
            System.out.println("Invitation sent successfully");
            return ResponseEntity.ok().build();
        } catch (IllegalStateException e) {
            // Ownership check failed
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.status(403).body(null); // Explicit 403 with no body
        } catch (IllegalArgumentException e) {
            // User or workspace not found
            System.out.println("Error: " + e.getMessage());
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            // Unexpected errors (e.g., email sending failure)
            System.out.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping("/invitations/accept/{invitationId}")
    public ResponseEntity<Void> acceptInvitation(@PathVariable Long invitationId) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        workspaceService.acceptInvitation(invitationId, user);
        return ResponseEntity.ok().build();
    }
}