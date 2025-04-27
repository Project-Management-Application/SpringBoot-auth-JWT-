package com.midou.tutorial.Projects.controller;


import com.midou.tutorial.Projects.DTO.CreateLabelCategoryRequest;
import com.midou.tutorial.Projects.DTO.CreateLabelRequest;
import com.midou.tutorial.Projects.DTO.LabelCategoryDTO;
import com.midou.tutorial.Projects.DTO.UpdateLabelRequest;
import com.midou.tutorial.Projects.entities.ProjectTaskLabel;
import com.midou.tutorial.Projects.entities.ProjectTaskLabelCategory;
import com.midou.tutorial.Projects.services.LabelService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
@CrossOrigin(origins = "${frontend.url}")

@RestController
@RequestMapping("/api/v1/projects/labels")
@RequiredArgsConstructor
public class LabelController {

    @Autowired
    private LabelService labelService;

    // Create predefined labels (admin or initialization endpoint)
    @PostMapping("/CreateLabel")
    public ResponseEntity<LabelCategoryDTO> createLabelCategory(@Valid @RequestBody CreateLabelCategoryRequest request) {
        LabelCategoryDTO category = labelService.createLabelCategory(request);
        return ResponseEntity.ok(category);
    }




    // Fetch all labels
    @GetMapping("/FetchAllLabels")
    public ResponseEntity<List<LabelCategoryDTO>> fetchAllLabels() {
        try {
            List<LabelCategoryDTO> categories = labelService.fetchAllLabels();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    // Delete a label
    @DeleteMapping("/DeleteLabel/{labelId}")
    public ResponseEntity<String> deleteLabel(@PathVariable Long labelId) {
        try {
            labelService.deleteLabel(labelId);
            return ResponseEntity.ok("Label deleted successfully");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete label: " + e.getMessage());
        }
    }

    // Update a label
    @PutMapping("/UpdateLabel/{labelId}")
    public ResponseEntity<ProjectTaskLabel> updateLabel(
            @PathVariable Long labelId,
            @Valid @RequestBody UpdateLabelRequest request) {
        try {
            ProjectTaskLabel updatedLabel = labelService.updateLabel(
                    labelId,
                    request.getTagValue(),
                    request.getColor()
            );
            return ResponseEntity.ok(updatedLabel);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(403).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }
}





