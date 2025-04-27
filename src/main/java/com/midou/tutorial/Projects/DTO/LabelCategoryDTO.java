package com.midou.tutorial.Projects.DTO;


import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
public class LabelCategoryDTO {
    private Long id;
    private String name;
    private boolean isDefault;
    private List<LabelDTO> labels;

}