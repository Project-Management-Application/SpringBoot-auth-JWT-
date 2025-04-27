package com.midou.tutorial.Projects.DTO;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskSummaryDTO {
    private Long id;
    private String name;
}