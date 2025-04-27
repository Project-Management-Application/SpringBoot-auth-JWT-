package com.midou.tutorial.Projects.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskDescriptionRequestDTO {
    @NotBlank(message = "Description cannot be blank")
    private String description;
}