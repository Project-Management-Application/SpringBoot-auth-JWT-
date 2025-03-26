package com.midou.tutorial.backlog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class createChecklistItemDTO {
    private long checklistId;
    private String title;

}
