package com.midou.tutorial.Projects.DTO;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistDTO {
    private Long id;
    private String title;
    private List<ChecklistItemDTO> items;
}