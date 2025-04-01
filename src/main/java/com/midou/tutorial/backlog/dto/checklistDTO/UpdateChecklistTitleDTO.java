package com.midou.tutorial.backlog.dto.checklistDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateChecklistTitleDTO {
    private long checklistId;
    private String title;
}
