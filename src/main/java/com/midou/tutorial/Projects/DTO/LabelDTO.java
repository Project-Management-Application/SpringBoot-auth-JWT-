package com.midou.tutorial.Projects.DTO;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LabelDTO {
    private Long id;
    private String tagValue;
    private String color;
    private boolean isDefault;
    private Long categoryId; // Assuming this is the 5th argument mentioned in the error
}