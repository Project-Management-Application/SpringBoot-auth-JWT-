package com.midou.tutorial.Models.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModelDTO {
    private Long id;
    private String name;
    private String backgroundImage;
}