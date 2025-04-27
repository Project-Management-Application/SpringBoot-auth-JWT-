package com.midou.tutorial.Projects.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UpdateTaskCoverResponseDTO {
    private Long taskId;
    private String coverImage;
    private String coverColor;
    private String message;
}