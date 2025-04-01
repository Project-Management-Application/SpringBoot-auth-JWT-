package com.midou.tutorial.Workspace.DTO;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
@Getter
@Setter
public class ProjectDTO {
    private Long id;
    private String name;
    private String visibility;
    private String backgroundImage;
    private String backgroundColor;
    private Long modelId;
    private String modelBackgroundImage;


    public ProjectDTO(Long id, String name, String visibility,String backgroundImage, String backgroundColor,Long modelId,String modelBackgroundImage) {
        this.id = id;
        this.name = name;
        this.visibility = visibility;
        this.backgroundImage = backgroundImage;
        this.backgroundColor = backgroundColor;
        this.modelId = modelId;
        this.modelBackgroundImage = modelBackgroundImage;
    }



}