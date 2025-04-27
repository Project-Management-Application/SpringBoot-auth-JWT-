package com.midou.tutorial.Projects.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MoveTaskRequestDTO {
    @NotNull(message = "Target card ID cannot be null")
    private Long cardId;
}