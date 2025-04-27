package com.midou.tutorial.Projects.DTO;

import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AssignLabelsRequest {
    @NotEmpty(message = "At least one label ID is required")
    private List<Long> labelIds;
}