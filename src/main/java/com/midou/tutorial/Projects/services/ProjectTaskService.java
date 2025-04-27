package com.midou.tutorial.Projects.services;

import com.midou.tutorial.Projects.DTO.*;
import com.midou.tutorial.Projects.entities.*;
import com.midou.tutorial.Projects.enums.TaskStatus;
import com.midou.tutorial.Projects.repositories.*;

import com.midou.tutorial.Storage.SupabaseStorage;
import com.midou.tutorial.user.entities.User;
import com.midou.tutorial.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ProjectTaskService {

    @Autowired
    private ProjectTaskRepository projectTaskRepository;

    @Autowired
    private ProjectCardRepository projectCardRepository;
    @Autowired
    private ProjectTaskRepository ProjectTaskRepository;
    @Autowired
    private ProjectTaskLabelRepository projectTaskLabelRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProjectMemberRepository projectMemberRepository;
    @Autowired
    private ProjectTaskChecklistRepository projectTaskChecklistRepository;

    @Autowired
    private ProjectCheckListItemRepository projectCheckListItemRepository;
    @Autowired
    private ProjectTaskCommentRepository projectTaskCommentRepository;
    @Autowired
    private SupabaseStorage supabaseStorage;
    private static final Pattern HEX_COLOR_PATTERN = Pattern.compile("^#[0-9A-Fa-f]{6}$");
    private static final Pattern URL_PATTERN = Pattern.compile("^(https?://).+");

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth.getPrincipal().equals("anonymousUser")) {
            throw new IllegalStateException("User not authenticated");
        }
        String email = auth.getName(); // Assumes JWT subject is the user's email
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalStateException("Authenticated user not found in database"));
    }

    @Transactional
    public TaskCreationResponseDTO createTask(Long cardId, ProjectTaskCreateDTO taskDTO) {
        // Validate card exists
        ProjectCard card = projectCardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("ProjectCard with ID " + cardId + " not found"));

        // Create task entity
        ProjectTask task = ProjectTask.builder()
                .name(taskDTO.getName())
                .status(TaskStatus.PENDING) // Default status
                .card(card)
                .build();

        // Save task
        ProjectTask savedTask = projectTaskRepository.save(task);

        // Return response with ID and message
        return TaskCreationResponseDTO.builder()
                .id(savedTask.getId())
                .message("Task created successfully")
                .build();
    }


    @Transactional
    public TaskCreationResponseDTO moveTask(Long taskId, MoveTaskRequestDTO moveTaskDTO) {
        // Validate task exists
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + taskId + " not found"));

        // Validate target card exists
        ProjectCard targetCard = projectCardRepository.findById(moveTaskDTO.getCardId())
                .orElseThrow(() -> new IllegalArgumentException("Target ProjectCard with ID " + moveTaskDTO.getCardId() + " not found"));

        // Update task's card
        task.setCard(targetCard);

        // Save updated task
        ProjectTask updatedTask = projectTaskRepository.save(task);

        // Return response
        return TaskCreationResponseDTO.builder()
                .id(updatedTask.getId())
                .message("Task moved successfully")
                .build();
    }


    @Transactional(readOnly = true)
    public List<ProjectTaskSummaryDTO> getTasksByCardId(Long cardId) {
        projectCardRepository.findById(cardId)
                .orElseThrow(() -> new IllegalArgumentException("ProjectCard with ID " + cardId + " not found"));
        List<ProjectTask> tasks = projectTaskRepository.findByCardId(cardId);
        return tasks.stream()
                .map(task -> ProjectTaskSummaryDTO.builder()
                        .id(task.getId())
                        .name(task.getName())
                        .build())
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public ProjectTaskDetailsDTO getTaskDetails(Long taskId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + taskId + " not found"));

        return ProjectTaskDetailsDTO.builder()
                .id(task.getId())
                .name(task.getName())
                .description(task.getDescription())
                .coverImage(task.getCoverImage())
                .coverColor(task.getCoverColor())
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .dueDateReminder(task.getDueDateReminder())
                .status(task.getStatus())
                .cardId(task.getCard().getId())
                .assignedMemberIds(task.getAssignedMembers().stream()
                        .map(user -> user.getId())
                        .collect(Collectors.toList()))
                .labels(task.getProjectTaskLabels().stream()
                        .map(label -> ProjectTaskDetailsDTO.ProjectTaskLabelDTO.builder()
                                .id(label.getId())
                                .tagValue(label.getTagValue())
                                .color(label.getColor())
                                .categoryId(label.getCategory().getId())
                                .isDefault(label.isDefault())
                                .build())
                        .collect(Collectors.toList()))
                .comments(task.getProjectTaskComments().stream()
                        .map(comment -> ProjectTaskDetailsDTO.ProjectTaskCommentDTO.builder()
                                .id(comment.getId())
                                .content(comment.getContent())
                                .userId(comment.getAuthor().getId())
                                .createdAt(comment.getCreatedAt())
                                .updatedAt(comment.getUpdatedAt())
                                .build())
                        .collect(Collectors.toList()))
                .attachments(task.getProjectTaskAttachments().stream()
                        .map(attachment -> ProjectTaskDetailsDTO.ProjectTaskAttachmentDTO.builder()
                                .id(attachment.getId())
                                .fileName(attachment.getFileName())
                                .fileUrl(attachment.getFileUrl())
                                .fileType(attachment.getFileType())
                                .fileSize(attachment.getFileSize())
                                .uploadedById(attachment.getUploadedBy().getId())
                                .uploadedAt(attachment.getUploadedAt())
                                .build())
                        .collect(Collectors.toList()))
                .checklists(task.getChecklists().stream()
                        .map(checklist -> ProjectTaskDetailsDTO.ProjectTaskChecklistDTO.builder()
                                .id(checklist.getId())
                                .title(checklist.getTitle())
                                .items(checklist.getItems().stream()
                                        .map(item -> ProjectTaskDetailsDTO.ProjectCheckListItemDTO.builder()
                                                .id(item.getId())
                                                .content(item.getContent())
                                                .isCompleted(item.isCompleted())
                                                .assignedToId(item.getAssignedTo() != null ? item.getAssignedTo().getId() : null)
                                                .build())
                                        .collect(Collectors.toList()))
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public TaskCreationResponseDTO addTaskDescription(Long taskId, TaskDescriptionRequestDTO descriptionDTO) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + taskId + " not found"));

        if (task.getDescription() != null) {
            throw new IllegalStateException("Task already has a description");
        }

        task.setDescription(descriptionDTO.getDescription());
        ProjectTask updatedTask = projectTaskRepository.save(task);

        return TaskCreationResponseDTO.builder()
                .id(updatedTask.getId())
                .message("Task description added successfully")
                .build();
    }

    @Transactional
    public TaskCreationResponseDTO updateTaskDescription(Long taskId, TaskDescriptionRequestDTO descriptionDTO) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task with ID " + taskId + " not found"));

        if (task.getDescription() == null) {
            throw new IllegalStateException("Task does not have a description to update");
        }

        task.setDescription(descriptionDTO.getDescription());
        ProjectTask updatedTask = projectTaskRepository.save(task);

        return TaskCreationResponseDTO.builder()
                .id(updatedTask.getId())
                .message("Task description updated successfully")
                .build();
    }


    @Transactional
    public List<LabelDTO> assignLabelsToTask(Long taskId, AssignLabelsRequest request) {
        // Validate task
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Validate labels
        List<ProjectTaskLabel> labels = projectTaskLabelRepository.findAllById(request.getLabelIds());
        if (labels.size() != request.getLabelIds().size()) {
            throw new IllegalArgumentException("One or more label IDs are invalid");
        }

        // Add labels to task (avoid duplicates)
        List<ProjectTaskLabel> currentLabels = task.getProjectTaskLabels();
        for (ProjectTaskLabel label : labels) {
            if (!currentLabels.contains(label)) {
                currentLabels.add(label);
            }
        }

        // Save task with updated labels
        projectTaskRepository.save(task);

        // Map to DTO
        return currentLabels.stream().map(label -> LabelDTO.builder()
                        .id(label.getId())
                        .tagValue(label.getTagValue())
                        .color(label.getColor())
                        .isDefault(label.isDefault())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskCreationResponseDTO removeLabelFromTask(Long taskId, Long labelId) {
        // Validate task
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Validate label
        ProjectTaskLabel label = projectTaskLabelRepository.findById(labelId)
                .orElseThrow(() -> new IllegalArgumentException("Label not found"));

        // Check if label is assigned to task
        List<ProjectTaskLabel> currentLabels = task.getProjectTaskLabels();
        if (!currentLabels.contains(label)) {
            throw new IllegalArgumentException("Label is not assigned to the task");
        }

        // Remove label from task
        currentLabels.remove(label);

        // Save task to update task_labels
        projectTaskRepository.save(task);

        // Return response
        return TaskCreationResponseDTO.builder()
                .id(task.getId())
                .message("Label removed from task successfully")
                .build();
    }


    @Transactional
    public List<AssignedTaskMemberDTO> assignMembersToTask(Long taskId, AssignMembersToTaskRequest request) {
        // Validate task
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Get project ID from task
        Long projectId = task.getCard().getProject().getId();

        // Validate users and project membership
        List<User> users = userRepository.findAllById(request.getUserIds());
        if (users.size() != request.getUserIds().size()) {
            throw new IllegalArgumentException("One or more user IDs are invalid");
        }

        // Check if all users are project members
        List<Long> projectMemberIds = projectMemberRepository.findByProjectId(projectId)
                .stream()
                .map(member -> member.getUser().getId())
                .collect(Collectors.toList());

        for (User user : users) {
            if (!projectMemberIds.contains(user.getId())) {
                throw new IllegalArgumentException("User with ID " + user.getId() + " is not a member of the project");
            }
        }

        // Add users to task (avoid duplicates)
        List<User> currentMembers = task.getAssignedMembers();
        for (User user : users) {
            if (!currentMembers.contains(user)) {
                currentMembers.add(user);
            }
        }

        // Save task to update task_assigned_members
        projectTaskRepository.save(task);

        // Map to DTO
        return currentMembers.stream()
                .map(user -> AssignedTaskMemberDTO.builder()
                        .userId(user.getId())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .email(user.getEmail())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskCreationResponseDTO removeMemberFromTask(Long taskId, Long userId) {
        // Validate task
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Validate user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if user is assigned to task
        List<User> currentMembers = task.getAssignedMembers();
        if (!currentMembers.contains(user)) {
            throw new IllegalArgumentException("User is not assigned to the task");
        }

        // Remove user from task
        currentMembers.remove(user);

        // Save task to update task_assigned_members
        projectTaskRepository.save(task);

        // Return response
        return TaskCreationResponseDTO.builder()
                .id(task.getId())
                .message("Member removed from task successfully")
                .build();
    }


    //---------------Checklist Part------------//

    @Transactional
    public ChecklistDTO addChecklist(Long taskId, CreateChecklistRequest request) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        ProjectTaskChecklist checklist = ProjectTaskChecklist.builder()
                .title(request.getTitle())
                .task(task)
                .build();

        ProjectTaskChecklist savedChecklist = projectTaskChecklistRepository.save(checklist);

        return ChecklistDTO.builder()
                .id(savedChecklist.getId())
                .title(savedChecklist.getTitle())
                .items(List.of())
                .build();
    }

    @Transactional
    public ChecklistItemDTO addChecklistItem(Long taskId, Long checklistId, CreateChecklistItemRequest request) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        ProjectTaskChecklist checklist = projectTaskChecklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist not found"));

        if (!checklist.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("Checklist does not belong to the specified task");
        }

        ProjectCheckListItem item = ProjectCheckListItem.builder()
                .content(request.getContent())
                .isCompleted(false)
                .checklist(checklist)
                .build();

        ProjectCheckListItem savedItem = projectCheckListItemRepository.save(item);

        return ChecklistItemDTO.builder()
                .id(savedItem.getId())
                .content(savedItem.getContent())
                .isCompleted(savedItem.isCompleted())
                .build();
    }

    @Transactional
    public ChecklistDTO updateChecklistName(Long taskId, Long checklistId, UpdateChecklistRequest request) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        ProjectTaskChecklist checklist = projectTaskChecklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist not found"));

        if (!checklist.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("Checklist does not belong to the specified task");
        }

        checklist.setTitle(request.getTitle());
        ProjectTaskChecklist updatedChecklist = projectTaskChecklistRepository.save(checklist);

        return ChecklistDTO.builder()
                .id(updatedChecklist.getId())
                .title(updatedChecklist.getTitle())
                .items(updatedChecklist.getItems().stream()
                        .map(item -> ChecklistItemDTO.builder()
                                .id(item.getId())
                                .content(item.getContent())
                                .isCompleted(item.isCompleted())
                                .build())
                        .collect(Collectors.toList()))
                .build();
    }

    @Transactional
    public ChecklistItemDTO updateChecklistItemName(Long taskId, Long checklistId, Long itemId, UpdateChecklistItemRequest request) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        ProjectTaskChecklist checklist = projectTaskChecklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist not found"));

        if (!checklist.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("Checklist does not belong to the specified task");
        }

        ProjectCheckListItem item = projectCheckListItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist item not found"));

        if (!item.getChecklist().getId().equals(checklistId)) {
            throw new IllegalArgumentException("Item does not belong to the specified checklist");
        }

        item.setContent(request.getContent());
        ProjectCheckListItem updatedItem = projectCheckListItemRepository.save(item);

        return ChecklistItemDTO.builder()
                .id(updatedItem.getId())
                .content(updatedItem.getContent())
                .isCompleted(updatedItem.isCompleted())
                .build();
    }

    @Transactional
    public TaskCreationResponseDTO removeChecklistItem(Long taskId, Long checklistId, Long itemId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        ProjectTaskChecklist checklist = projectTaskChecklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist not found"));

        if (!checklist.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("Checklist does not belong to the specified task");
        }

        ProjectCheckListItem item = projectCheckListItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist item not found"));

        if (!item.getChecklist().getId().equals(checklistId)) {
            throw new IllegalArgumentException("Item does not belong to the specified checklist");
        }

        projectCheckListItemRepository.delete(item);

        return TaskCreationResponseDTO.builder()
                .id(taskId)
                .message("Checklist item removed successfully")
                .build();
    }

    @Transactional
    public TaskCreationResponseDTO deleteChecklist(Long taskId, Long checklistId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        ProjectTaskChecklist checklist = projectTaskChecklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist not found"));

        if (!checklist.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("Checklist does not belong to the specified task");
        }

        projectTaskChecklistRepository.delete(checklist);

        return TaskCreationResponseDTO.builder()
                .id(taskId)
                .message("Checklist deleted successfully")
                .build();
    }

    @Transactional
    public ChecklistItemDTO toggleChecklistItemStatus(Long taskId, Long checklistId, Long itemId, ToggleItemStatusRequest request) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        ProjectTaskChecklist checklist = projectTaskChecklistRepository.findById(checklistId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist not found"));

        if (!checklist.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("Checklist does not belong to the specified task");
        }

        ProjectCheckListItem item = projectCheckListItemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Checklist item not found"));

        if (!item.getChecklist().getId().equals(checklistId)) {
            throw new IllegalArgumentException("Item does not belong to the specified checklist");
        }

        item.setCompleted(request.isCompleted());
        ProjectCheckListItem updatedItem = projectCheckListItemRepository.save(item);

        return ChecklistItemDTO.builder()
                .id(updatedItem.getId())
                .content(updatedItem.getContent())
                .isCompleted(updatedItem.isCompleted())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ChecklistDTO> getAllChecklists(Long taskId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        List<ProjectTaskChecklist> checklists = projectTaskChecklistRepository.findByTaskId(taskId);

        return checklists.stream()
                .map(checklist -> ChecklistDTO.builder()
                        .id(checklist.getId())
                        .title(checklist.getTitle())
                        .items(checklist.getItems().stream()
                                .map(item -> ChecklistItemDTO.builder()
                                        .id(item.getId())
                                        .content(item.getContent())
                                        .isCompleted(item.isCompleted())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }


    //----------Comments Part-------//
    @Transactional
    public CommentDTO addComment(Long taskId, CreateCommentRequest request) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        User author = getCurrentUser();

        ProjectTaskComment comment = ProjectTaskComment.builder()
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(null)
                .author(author)
                .task(task)
                .build();

        ProjectTaskComment savedComment = projectTaskCommentRepository.save(comment);

        return CommentDTO.builder()
                .id(savedComment.getId())
                .content(savedComment.getContent())
                .userId(savedComment.getAuthor().getId())
                .createdAt(savedComment.getCreatedAt())
                .updatedAt(savedComment.getUpdatedAt())
                .build();
    }

    @Transactional
    public CommentDTO updateComment(Long taskId, Long commentId, UpdateCommentRequest request) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        ProjectTaskComment comment = projectTaskCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("Comment does not belong to the specified task");
        }

        User currentUser = getCurrentUser();
        if (currentUser == null || comment == null || comment.getAuthor() == null) {
            throw new IllegalStateException("Invalid state: current user or comment data is missing");
        }

        if (comment.getAuthor().getId() != currentUser.getId()) {
            throw new SecurityException("Only the comment author can update this comment");
        }


        comment.setContent(request.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        ProjectTaskComment updatedComment = projectTaskCommentRepository.save(comment);

        return CommentDTO.builder()
                .id(updatedComment.getId())
                .content(updatedComment.getContent())
                .userId(updatedComment.getAuthor().getId())
                .createdAt(updatedComment.getCreatedAt())
                .updatedAt(updatedComment.getUpdatedAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<CommentDTO> getAllComments(Long taskId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        List<ProjectTaskComment> comments = projectTaskCommentRepository.findByTaskId(taskId);

        return comments.stream()
                .map(comment -> CommentDTO.builder()
                        .id(comment.getId())
                        .content(comment.getContent())
                        .userId(comment.getAuthor().getId())
                        .createdAt(comment.getCreatedAt())
                        .updatedAt(comment.getUpdatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskCreationResponseDTO deleteComment(Long taskId, Long commentId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        ProjectTaskComment comment = projectTaskCommentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getTask().getId().equals(taskId)) {
            throw new IllegalArgumentException("Comment does not belong to the specified task");
        }

        User currentUser = getCurrentUser();
        if (currentUser == null || comment == null || comment.getAuthor() == null) {
            throw new IllegalStateException("Invalid state: current user or comment data is missing");
        }

        if (comment.getAuthor().getId() != currentUser.getId()) {
            throw new SecurityException("Only the comment author can delete this comment");
        }


        projectTaskCommentRepository.delete(comment);

        return TaskCreationResponseDTO.builder()
                .id(taskId)
                .message("Comment deleted successfully")
                .build();
    }


    @Transactional
    public CreateAttachmentResponseDTO addAttachment(Long taskId, MultipartFile file) throws Exception {
        // Validate task
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Validate file
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        // Generate unique filename (e.g., based on task ID and UUID)
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String uniqueFilename = String.format("tasks_files/%d/%s%s", taskId, UUID.randomUUID(), extension);

        // Upload the file to Supabase and get the file URL
        String fileUrl = supabaseStorage.uploadFileToSupabase(file, uniqueFilename);

        // Create attachment entity
        ProjectTaskAttachment attachment = ProjectTaskAttachment.builder()
                .fileName(originalFilename != null ? originalFilename : "unnamed_file")
                .fileUrl(fileUrl)
                .fileType(file.getContentType())
                .fileSize(file.getSize())
                .task(task)
                .uploadedBy(getCurrentUser())
                .uploadedAt(LocalDateTime.now())
                .build();

        // Save the attachment to the task
        task.getProjectTaskAttachments().add(attachment);
        projectTaskRepository.save(task);

        // Return the response DTO with attachment details
        return CreateAttachmentResponseDTO.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .fileUrl(attachment.getFileUrl())
                .fileType(attachment.getFileType())
                .fileSize(attachment.getFileSize())
                .taskId(taskId)
                .uploadedById(attachment.getUploadedBy().getId())
                .message("Attachment uploaded successfully")
                .build();
    }


    @Transactional
    public void removeAttachment(Long taskId, Long attachmentId) throws Exception {
        // Validate the task
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Find the attachment to remove
        ProjectTaskAttachment attachment = task.getProjectTaskAttachments().stream()
                .filter(a -> a.getId().equals(attachmentId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Attachment not found"));

        // Delete the file from Supabase Storage
        try {
            supabaseStorage.deleteFileFromSupabase(attachment.getFileUrl()); // Get the file name from the URL or attachment object
        } catch (IOException e) {
            throw new IOException("Failed to delete attachment file from Supabase: " + e.getMessage(), e);
        }

        // Remove the attachment from the task
        task.getProjectTaskAttachments().remove(attachment);

        // Save the task with the updated attachments list
        projectTaskRepository.save(task);

        // Optionally, log success
        System.out.println("Attachment removed successfully from task ID: " + taskId);
    }


    @Transactional
    public UpdateTaskCoverResponseDTO updateTaskCover(Long taskId, UpdateTaskCoverRequestDTO request) {
        // Validate task
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Validate request: either imageUrl or color, not both or neither
        if ((request.getImageUrl() != null && request.getColor() != null) ||
                (request.getImageUrl() == null && request.getColor() == null)) {
            throw new IllegalArgumentException("Provide either an image URL or a color, not both or neither");
        }

        String coverImage = null;
        String coverColor = null;

        if (request.getImageUrl() != null) {
            // Validate image URL
            if (!URL_PATTERN.matcher(request.getImageUrl()).matches()) {
                throw new IllegalArgumentException("Invalid image URL. Must start with http:// or https://");
            }
            coverImage = request.getImageUrl();
        } else {
            // Validate color
            if (!HEX_COLOR_PATTERN.matcher(request.getColor()).matches()) {
                throw new IllegalArgumentException("Invalid hex color code. Use format #RRGGBB (e.g., #FF0000)");
            }
            coverColor = request.getColor();
        }

        // Update task
        task.setCoverImage(coverImage);
        task.setCoverColor(coverColor);
        projectTaskRepository.save(task);

        // Return response
        return UpdateTaskCoverResponseDTO.builder()
                .taskId(taskId)
                .coverImage(coverImage)
                .coverColor(coverColor)
                .message("Task cover updated successfully")
                .build();
    }

    @Transactional
    public TaskDatesResponseDTO setTaskDates(Long taskId, SetTaskDatesRequestDTO request) {
        // Validate task
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Validate dates
        if (request.getStartDate() != null && request.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        if (request.getDueDateReminder() != null && request.getDueDate() != null &&
                !request.getDueDateReminder().isBefore(request.getDueDate())) {
            throw new IllegalArgumentException("Due date reminder must be before due date");
        }

        // Set dates
        task.setStartDate(request.getStartDate());
        task.setDueDate(request.getDueDate());
        task.setDueDateReminder(request.getDueDateReminder());
        projectTaskRepository.save(task);

        // Return response
        return TaskDatesResponseDTO.builder()
                .taskId(taskId)
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .dueDateReminder(task.getDueDateReminder())
                .message("Task dates set successfully")
                .build();
    }

    @Transactional
    public TaskDatesResponseDTO resetTaskDates(Long taskId, Set<String> fields) {
        // Validate task
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        // Validate fields
        if (fields.isEmpty()) {
            throw new IllegalArgumentException("At least one field must be specified to reset");
        }
        for (String field : fields) {
            if (!Set.of("startDate", "dueDate", "dueDateReminder").contains(field)) {
                throw new IllegalArgumentException("Invalid field: " + field);
            }
        }

        // Reset specified fields
        if (fields.contains("startDate")) {
            task.setStartDate(null);
        }
        if (fields.contains("dueDate")) {
            task.setDueDate(null);
        }
        if (fields.contains("dueDateReminder")) {
            task.setDueDateReminder(null);
        }
        projectTaskRepository.save(task);

        // Return response
        return TaskDatesResponseDTO.builder()
                .taskId(taskId)
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .dueDateReminder(task.getDueDateReminder())
                .message("Task dates reset successfully")
                .build();
    }

    @Transactional
    public TaskDatesResponseDTO updateTaskDates(Long taskId, UpdateTaskDatesRequestDTO request) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (request.getStartDate() != null && request.getStartDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past");
        }
        if (request.getDueDate() != null && request.getDueDate().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Due date cannot be in the past");
        }
        if (request.getDueDateReminder() != null && request.getDueDate() != null &&
                !request.getDueDateReminder().isBefore(request.getDueDate())) {
            throw new IllegalArgumentException("Due date reminder must be before due date");
        }

        if (request.getStartDate() != null) {
            task.setStartDate(request.getStartDate());
        }
        if (request.getDueDate() != null) {
            task.setDueDate(request.getDueDate());
        }
        if (request.getDueDateReminder() != null) {
            task.setDueDateReminder(request.getDueDateReminder());
        }
        projectTaskRepository.save(task);

        return TaskDatesResponseDTO.builder()
                .taskId(taskId)
                .startDate(task.getStartDate())
                .dueDate(task.getDueDate())
                .dueDateReminder(task.getDueDateReminder())
                .message("Task dates updated successfully")
                .build();
    }

    @Transactional
    public TaskOperationResponseDTO deleteTask(Long taskId) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        projectTaskRepository.delete(task);

        return TaskOperationResponseDTO.builder()
                .taskId(taskId)
                .message("Task deleted successfully")
                .build();
    }

    @Transactional
    public TaskOperationResponseDTO updateTaskName(Long taskId, UpdateTaskNameRequestDTO request) {
        ProjectTask task = projectTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found"));

        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Task name cannot be empty");
        }
        if (request.getName().length() > 255) {
            throw new IllegalArgumentException("Task name cannot exceed 255 characters");
        }

        task.setName(request.getName().trim());
        projectTaskRepository.save(task);

        return TaskOperationResponseDTO.builder()
                .taskId(taskId)
                .message("Task name updated successfully")
                .build();
    }

}