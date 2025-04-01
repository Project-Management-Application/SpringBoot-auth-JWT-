package com.midou.tutorial.Models.controller;

import com.midou.tutorial.Models.DTO.ModelDTO;
import com.midou.tutorial.Models.entities.Model;
import com.midou.tutorial.Models.services.ModelService;
import com.midou.tutorial.Models.DTO.ModelRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@CrossOrigin(origins = "${frontend.url}")
@RestController
@RequestMapping("/api/v1/models")
@RequiredArgsConstructor
public class ModelController {

    @Autowired
    private ModelService modelService;

    @PostMapping(value = "/createModel", consumes = "multipart/form-data")
    public ResponseEntity<?> createModel(
            @RequestParam("name") String name,
            @RequestParam(value = "backgroundImage") MultipartFile backgroundImage,
            @RequestParam("cardNames") List<String> cardNames) {
        try {
            if (name == null || name.isEmpty()) {
                return ResponseEntity.badRequest().body("Name is required");
            }
            if (backgroundImage == null || backgroundImage.isEmpty()) {
                return ResponseEntity.badRequest().body("Background image is required");
            }
            if (cardNames == null || cardNames.isEmpty()) {
                return ResponseEntity.badRequest().body("At least one card name is required");
            }

            System.out.println("Name: " + name);
            System.out.println("BackgroundImage: " + backgroundImage.getOriginalFilename());
            System.out.println("CardNames: " + cardNames);

            ModelRequest request = new ModelRequest();
            request.setName(name);
            request.setBackgroundImage(backgroundImage);
            request.setCardNames(cardNames);

            Model model = modelService.createModel(request);
            return ResponseEntity.ok(model);
        } catch (IOException e) {
            System.err.println("Error creating model: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to upload image: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body("An unexpected error occurred: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<ModelDTO>> getAllModels() {
        try {
            List<ModelDTO> models = modelService.getAllModels();
            return ResponseEntity.ok(models);
        } catch (Exception e) {
            System.err.println("Error fetching models: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}