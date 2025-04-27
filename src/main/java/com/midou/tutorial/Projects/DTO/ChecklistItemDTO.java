package com.midou.tutorial.Projects.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItemDTO {
    private Long id;
    private String content;
    private boolean isCompleted;
}