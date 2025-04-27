package com.midou.tutorial.Projects.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
public class ToggleItemStatusRequest {
    @JsonProperty("isCompleted") // Explicitly map JSON field to this property
    private boolean isCompleted;
}