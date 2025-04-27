package com.midou.tutorial.Projects.DTO;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class UpdateTaskCoverRequestDTO {
    private String imageUrl;
    private String color;
}