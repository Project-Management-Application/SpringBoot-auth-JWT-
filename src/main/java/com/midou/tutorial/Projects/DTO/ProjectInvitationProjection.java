

package com.midou.tutorial.Projects.DTO;

import java.time.LocalDateTime;

public interface ProjectInvitationProjection {
    Long getId();
    String getProjectName(); // Maps to project.name
    LocalDateTime getExpiresAt();
}
