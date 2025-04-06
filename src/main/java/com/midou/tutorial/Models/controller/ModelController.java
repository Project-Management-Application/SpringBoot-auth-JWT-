package com.midou.tutorial.Models.controller;

import com.midou.tutorial.Models.DTO.ModelDTO;
import com.midou.tutorial.Models.services.ModelService;
import com.midou.tutorial.Models.DTO.ModelRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.util.CollectionUtils;
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
            @RequestParam("description") String description,
            @RequestParam("backgroundImage") MultipartFile backgroundImage,
            @RequestParam("cardNames") List<String> cardNames) {

        if (!StringUtils.hasText(name)) {
            return ResponseEntity.badRequest().body("Name is required");
        }

        if (!StringUtils.hasText(description)) {
            return ResponseEntity.badRequest().body("Description is required");
        }

        if (backgroundImage == null || backgroundImage.isEmpty()) {
            return ResponseEntity.badRequest().body("Background image is required");
        }

        if (CollectionUtils.isEmpty(cardNames)) {
            return ResponseEntity.badRequest().body("At least one card name is required");
        }

        try {
            ModelRequest request = new ModelRequest();
            request.setName(name);
            request.setDescription(description);
            request.setBackgroundImage(backgroundImage);
            request.setCardNames(cardNames);

            modelService.createModel(request);
            return ResponseEntity.ok("Model created successfully");
        } catch (IOException e) {
            System.err.println("Error uploading image: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to upload image");
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return ResponseEntity.status(500).body("An unexpected error occurred");
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