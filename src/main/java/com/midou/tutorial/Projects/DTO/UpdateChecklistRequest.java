package com.midou.tutorial.Projects.DTO;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateChecklistRequest {
    @NotBlank(message = "Title is required")
    private String title;
}