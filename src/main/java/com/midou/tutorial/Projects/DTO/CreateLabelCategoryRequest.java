package com.midou.tutorial.Projects.DTO;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public class CreateLabelCategoryRequest {
    @NotBlank(message = "Category name is required")
    private String categoryName;

    @NotNull(message = "Default status is required")
    private Boolean isDefault;

    @NotNull(message = "Labels are required")
    private List<LabelRequest> labels;

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public List<LabelRequest> getLabels() {
        return labels;
    }

    public void setLabels(List<LabelRequest> labels) {
        this.labels = labels;
    }

    public static class LabelRequest {
        @NotBlank(message = "Tag value is required")
        private String tagValue;

        @NotBlank(message = "Color is required")
        @Pattern(regexp = "^#[0-9A-Fa-f]{6}$", message = "Color must be a valid hex code")
        private String color;

        @NotNull(message = "Default status is required")
        private Boolean isDefault;

        public String getTagValue() {
            return tagValue;
        }

        public void setTagValue(String tagValue) {
            this.tagValue = tagValue;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }

        public Boolean getIsDefault() {
            return isDefault;
        }

        public void setIsDefault(Boolean isDefault) {
            this.isDefault = isDefault;
        }
    }
}