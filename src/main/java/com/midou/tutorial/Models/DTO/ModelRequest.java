package com.midou.tutorial.Models.DTO;



import lombok.Data;

import java.util.List;


import org.springframework.web.multipart.MultipartFile;



@Data
public class ModelRequest {
    private String name;
    private MultipartFile backgroundImage;
    private List<String> cardNames;
}