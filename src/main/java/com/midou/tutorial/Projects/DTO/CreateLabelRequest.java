package com.midou.tutorial.Projects.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
 public class CreateLabelRequest {
    private String categoryName;
    private String tagValue;
    private String color;
    private boolean isNewCategory;


}