package com.midou.tutorial.Projects.services;

import com.midou.tutorial.Projects.DTO.CreateLabelCategoryRequest;
import com.midou.tutorial.Projects.DTO.LabelCategoryDTO;
import com.midou.tutorial.Projects.DTO.LabelDTO;
import com.midou.tutorial.Projects.entities.ProjectTaskLabel;
import com.midou.tutorial.Projects.entities.ProjectTaskLabelCategory;
import com.midou.tutorial.Projects.repositories.ProjectTaskLabelCategoryRepository;
import com.midou.tutorial.Projects.repositories.ProjectTaskLabelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LabelService {

    @Autowired
    private ProjectTaskLabelRepository labelRepository;

    @Autowired
    private ProjectTaskLabelCategoryRepository categoryRepository;

    // Create predefined labels (called during app initialization)

    // Create a label category with labels based on user input
    @Transactional
    public LabelCategoryDTO createLabelCategory(CreateLabelCategoryRequest request) {
        // Check if category already exists
        Optional<ProjectTaskLabelCategory> existingCategory = categoryRepository.findByName(request.getCategoryName());
        ProjectTaskLabelCategory category;

        if (existingCategory.isPresent()) {
            category = existingCategory.get();
            if (category.isDefault() != request.getIsDefault()) {
                throw new IllegalArgumentException("Cannot change default status of existing category");
            }
        } else {
            category = ProjectTaskLabelCategory.builder()
                    .name(request.getCategoryName())
                    .isDefault(request.getIsDefault())
                    .projectTaskLabels(new ArrayList<>()) // Ensure list is initialized
                    .build();
            category = categoryRepository.save(category);
        }

        // Create labels
        for (CreateLabelCategoryRequest.LabelRequest labelRequest : request.getLabels()) {
            if (labelRepository.existsByCategoryIdAndTagValue(category.getId(), labelRequest.getTagValue())) {
                throw new IllegalArgumentException("Tag '" + labelRequest.getTagValue() + "' already exists in category '" + category.getName() + "'");
            }

            ProjectTaskLabel label = ProjectTaskLabel.builder()
                    .tagValue(labelRequest.getTagValue())
                    .color(labelRequest.getColor())
                    .category(category)
                    .isDefault(labelRequest.getIsDefault())
                    .build();
            labelRepository.save(label);
        }

        // Refresh category to ensure projectTaskLabels is populated
        category = categoryRepository.findById(category.getId())
                .orElseThrow(() -> new IllegalStateException("Category not found after saving labels"));

        // Map to DTO for response
        LabelCategoryDTO dto = new LabelCategoryDTO();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDefault(category.isDefault());
        dto.setLabels(category.getProjectTaskLabels().stream().map(label -> {
            LabelDTO labelDTO = new LabelDTO();
            labelDTO.setId(label.getId());
            labelDTO.setTagValue(label.getTagValue());
            labelDTO.setColor(label.getColor());
            labelDTO.setDefault(label.isDefault());
            return labelDTO;
        }).collect(Collectors.toList()));

        return dto;
    }




    // Fetch all labels
    @Transactional(readOnly = true)
    public List<LabelCategoryDTO> fetchAllLabels() {
        List<ProjectTaskLabelCategory> categories = categoryRepository.findAll();
        return categories.stream().map(category -> {
            LabelCategoryDTO dto = new LabelCategoryDTO();
            dto.setId(category.getId());
            dto.setName(category.getName());
            dto.setDefault(category.isDefault());
            dto.setLabels(category.getProjectTaskLabels().stream().map(label -> {
                LabelDTO labelDTO = new LabelDTO();
                labelDTO.setId(label.getId());
                labelDTO.setTagValue(label.getTagValue());
                labelDTO.setColor(label.getColor());
                labelDTO.setDefault(label.isDefault());
                return labelDTO;
            }).collect(Collectors.toList()));
            return dto;
        }).collect(Collectors.toList());
    }


    // Delete a label
    @Transactional
    public void deleteLabel(Long labelId) {
        ProjectTaskLabel label = labelRepository.findById(labelId)
                .orElseThrow(() -> new IllegalArgumentException("Label not found"));

        if (label.isDefault()) {
            throw new IllegalStateException("Cannot delete default labels");
        }

        labelRepository.delete(label);
    }

    // Update a label
    @Transactional
    public ProjectTaskLabel updateLabel(Long labelId, String tagValue, String color) {
        ProjectTaskLabel label = labelRepository.findById(labelId)
                .orElseThrow(() -> new IllegalArgumentException("Label not found"));

        if (label.isDefault()) {
            throw new IllegalStateException("Cannot update default labels");
        }

        if (tagValue != null && !tagValue.trim().isEmpty()) {
            if (!tagValue.equals(label.getTagValue()) &&
                    labelRepository.existsByCategoryIdAndTagValue(label.getCategory().getId(), tagValue)) {
                throw new IllegalArgumentException("Tag already exists in this category");
            }
            label.setTagValue(tagValue);
        }

        if (color != null && !color.trim().isEmpty()) {
            label.setColor(color);
        }

        return labelRepository.save(label);
    }
}