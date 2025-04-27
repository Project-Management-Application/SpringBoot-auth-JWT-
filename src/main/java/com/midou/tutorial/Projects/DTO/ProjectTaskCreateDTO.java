package com.midou.tutorial.Projects.DTO;

import lombok.*;

import jakarta.validation.constraints.NotBlank;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskCreateDTO {
    @NotBlank(message = "Task name cannot be empty")
    private String name;
}