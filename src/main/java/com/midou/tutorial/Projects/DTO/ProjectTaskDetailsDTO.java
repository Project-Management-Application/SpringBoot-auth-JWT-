package com.midou.tutorial.Projects.DTO;


import com.midou.tutorial.Projects.enums.TaskStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskDetailsDTO {
    private Long id;
    private String name;
    private String description;
    private String coverImage;
    private String coverColor;
    private LocalDateTime startDate;
    private LocalDateTime dueDate;
    private LocalDateTime dueDateReminder;
    private TaskStatus status;
    private Long cardId;
    private List<Long> assignedMemberIds;
    private List<ProjectTaskLabelDTO> labels;
    private List<ProjectTaskCommentDTO> comments;
    private List<ProjectTaskAttachmentDTO> attachments;
    private List<ProjectTaskChecklistDTO> checklists;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectTaskLabelDTO {
        private Long id;
        private String tagValue;
        private String color;
        private Long categoryId;
        private boolean isDefault;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectTaskCommentDTO {
        private Long id;
        private String content;
        private Long userId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectTaskAttachmentDTO {
        private Long id;
        private String fileName;
        private String fileUrl;
        private String fileType;
        private Long fileSize;
        private Long uploadedById;
        private LocalDateTime uploadedAt;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectTaskChecklistDTO {
        private Long id;
        private String title;
        private List<ProjectCheckListItemDTO> items;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ProjectCheckListItemDTO {
        private Long id;
        private String content;
        private boolean isCompleted;
        private Long assignedToId;
    }
}