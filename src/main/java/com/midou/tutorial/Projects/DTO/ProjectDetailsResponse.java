package com.midou.tutorial.Projects.DTO;

import com.midou.tutorial.Projects.enums.ProjectRole;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectDetailsResponse {
    private Long id;
    private String name;
    private String description;
    private String visibility;
    private String backgroundImage;
    private String backgroundColor;
    private Long workspaceId;
    private Long ownerId;
    private Long modelId;
    private String modelBackgroundImage;
    private List<ProjectCardDTO> cards;
    private List<ProjectMemberDTO> members;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectCardDTO {
        private Long id;
        private String name;
        private List<ProjectTaskSummaryDTO> tasks; // Added to include tasks
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectMemberDTO {
        private Long userId;
        private ProjectRole role;
    }
}