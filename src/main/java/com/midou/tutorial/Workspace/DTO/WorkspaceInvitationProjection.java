package com.midou.tutorial.Workspace.DTO;

import java.time.LocalDateTime;

public interface WorkspaceInvitationProjection {
    Long getId();
    String getWorkspaceName(); // Maps to workspace.name
    LocalDateTime getExpiresAt();
}