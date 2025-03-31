package com.midou.tutorial.Projects.DTO;



import lombok.Data;

@Data
public class ProjectCreateResponse {
    private String message;
    private Long id;
    private String name;
}