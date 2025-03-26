package com.midou.tutorial.backlog.dto.sprintDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSprintTitleDTO {
    private long sprintId;
    private String title;
}
