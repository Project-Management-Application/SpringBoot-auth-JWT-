package com.midou.tutorial.Workspace.DTO;

import java.time.LocalDateTime;

public class WorkspaceInvitationDTO {
    private Long id;
    private String workspaceName;
    private LocalDateTime expiresAt;

    public WorkspaceInvitationDTO(Long id, String workspaceName, LocalDateTime expiresAt) {
        this.id = id;
        this.workspaceName = workspaceName;
        this.expiresAt = expiresAt;
    }
    // Getters and setters...
}