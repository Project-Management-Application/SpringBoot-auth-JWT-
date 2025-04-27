package com.midou.tutorial.Projects.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskCreationResponseDTO {
    private Long id;
    private String message;
}