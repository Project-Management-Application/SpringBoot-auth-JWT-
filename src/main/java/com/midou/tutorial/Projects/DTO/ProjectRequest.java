package com.midou.tutorial.Projects.DTO;

import com.midou.tutorial.Projects.enums.Visibility;
import lombok.Data;

@Data
public class ProjectRequest {
    private String name;
    private String description;
    private Visibility visibility;
    private String backgroundImage;
    private String backgroundColor;
    private Long modelId;
    private Long workspaceId;
}