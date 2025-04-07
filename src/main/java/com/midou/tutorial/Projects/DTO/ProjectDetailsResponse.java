package com.midou.tutorial.Projects.DTO;

import com.midou.tutorial.Projects.enums.ProjectRole;
import com.midou.tutorial.Projects.enums.Visibility;
import com.midou.tutorial.user.enums.Role;
import lombok.Data;

import java.util.List;

@Data
public class ProjectDetailsResponse {
    private Long id;
    private String name;
    private String description;
    private Visibility visibility;
    private String backgroundImage;
    private String backgroundColor;
    private Long workspaceId;
    private Long ownerId;
    private Long modelId;
    private String modelBackgroundImage;
    private List<ProjectCardDTO> cards;
    private List<ProjectMemberDTO> members;

    @Data
    public static class ProjectCardDTO {
        private Long id;
        private String name;
    }

    @Data
    public static class ProjectMemberDTO {
        private Long userId;
        private ProjectRole role;


    }
}