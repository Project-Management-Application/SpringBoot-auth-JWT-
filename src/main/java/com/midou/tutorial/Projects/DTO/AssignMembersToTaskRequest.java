package com.midou.tutorial.Projects.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignMembersToTaskRequest {
    @NotEmpty(message = "At least one user ID is required")
    private List<Long> userIds;
}
