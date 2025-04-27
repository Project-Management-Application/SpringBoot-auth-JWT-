package com.midou.tutorial.Projects.DTO;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CardOperationResponseDTO {
    private Long cardId;
    private String message;
}