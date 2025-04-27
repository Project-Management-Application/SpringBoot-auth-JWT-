package com.midou.tutorial.Projects.controller;

import com.midou.tutorial.Projects.DTO.*;
import com.midou.tutorial.Projects.services.ProjectTaskService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;

@CrossOrigin(origins = "${frontend.url}")
@RestController
@RequestMapping("/api/v1/projects")
@RequiredArgsConstructor
public class ProjectTaskController {

    @Autowired
    private ProjectTaskService projectTaskService;


    @PostMapping("/CreateTask/{cardId}")
    public ResponseEntity<TaskCreationResponseDTO> createTask(
            @PathVariable Long cardId,
            @Valid @RequestBody ProjectTaskCreateDTO taskDTO) {
        TaskCreationResponseDTO response = projectTaskService.createTask(cardId, taskDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/cards/{cardId}/tasks")
    public ResponseEntity<List<ProjectTaskSummaryDTO>> getTasksByCardId(
            @PathVariable Long cardId) {
        List<ProjectTaskSummaryDTO> tasks = projectTaskService.getTasksByCardId(cardId);
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    @PatchMapping("/tasks/{taskId}/move")
    public ResponseEntity<TaskCreationResponseDTO> moveTask(
            @PathVariable Long taskId,
            @Valid @RequestBody MoveTaskRequestDTO moveTaskDTO) {
        TaskCreationResponseDTO response = projectTaskService.moveTask(taskId, moveTaskDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/FetchTaskInfo/{taskId}")
    public ResponseEntity<ProjectTaskDetailsDTO> getTaskDetails(
            @PathVariable Long taskId) {
        ProjectTaskDetailsDTO taskDetails = projectTaskService.getTaskDetails(taskId);
        return new ResponseEntity<>(taskDetails, HttpStatus.OK);
    }

    @PostMapping("/tasks/{taskId}/AddDescription")
    public ResponseEntity<TaskCreationResponseDTO> addTaskDescription(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskDescriptionRequestDTO descriptionDTO) {
        TaskCreationResponseDTO response = projectTaskService.addTaskDescription(taskId, descriptionDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/tasks/{taskId}/UpdateDescription")
    public ResponseEntity<TaskCreationResponseDTO> updateTaskDescription(
            @PathVariable Long taskId,
            @Valid @RequestBody TaskDescriptionRequestDTO descriptionDTO) {
        TaskCreationResponseDTO response = projectTaskService.updateTaskDescription(taskId, descriptionDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/tasks/{taskId}/Assignlabels")
    public ResponseEntity<List<LabelDTO>> assignLabelsToTask(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignLabelsRequest request) {
        List<LabelDTO> assignedLabels = projectTaskService.assignLabelsToTask(taskId, request);
        return ResponseEntity.ok(assignedLabels);
    }
    @DeleteMapping("/tasks/{taskId}/RemoveLabelFromTask/{labelId}")
    public ResponseEntity<TaskCreationResponseDTO> removeLabelFromTask(
            @PathVariable Long taskId,
            @PathVariable Long labelId) {
        TaskCreationResponseDTO response = projectTaskService.removeLabelFromTask(taskId, labelId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/tasks/{taskId}/AssignmembersToTask")
    public ResponseEntity<List<AssignedTaskMemberDTO>> assignMembersToTask(
            @PathVariable Long taskId,
            @Valid @RequestBody AssignMembersToTaskRequest request) {
        List<AssignedTaskMemberDTO> assignedMembers = projectTaskService.assignMembersToTask(taskId, request);
        return ResponseEntity.ok(assignedMembers);
    }

    @DeleteMapping("/tasks/{taskId}/RemoveMembersFromTask/{userId}")
    public ResponseEntity<TaskCreationResponseDTO> removeMemberFromTask(
            @PathVariable Long taskId,
            @PathVariable Long userId) {
        TaskCreationResponseDTO response = projectTaskService.removeMemberFromTask(taskId, userId);
        return ResponseEntity.ok(response);
    }


    //----------CHECKLIST PART---------------------------//
    @PostMapping("/tasks/{taskId}/CreateChecklists")
    public ResponseEntity<ChecklistDTO> addChecklist(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateChecklistRequest request) {
        ChecklistDTO checklist = projectTaskService.addChecklist(taskId, request);
        return ResponseEntity.ok(checklist);
    }

    @PostMapping("/tasks/{taskId}/AddChecklistItem/{checklistId}/items")
    public ResponseEntity<ChecklistItemDTO> addChecklistItem(
            @PathVariable Long taskId,
            @PathVariable Long checklistId,
            @Valid @RequestBody CreateChecklistItemRequest request) {
        ChecklistItemDTO item = projectTaskService.addChecklistItem(taskId, checklistId, request);
        return ResponseEntity.ok(item);
    }

    @PutMapping("/tasks/{taskId}/UpdateChecklistName/{checklistId}")
    public ResponseEntity<ChecklistDTO> updateChecklistName(
            @PathVariable Long taskId,
            @PathVariable Long checklistId,
            @Valid @RequestBody UpdateChecklistRequest request) {
        ChecklistDTO checklist = projectTaskService.updateChecklistName(taskId, checklistId, request);
        return ResponseEntity.ok(checklist);
    }

    @PutMapping("/tasks/{taskId}/UpdateChecklistItemName/{checklistId}/items/{itemId}")
    public ResponseEntity<ChecklistItemDTO> updateChecklistItemName(
            @PathVariable Long taskId,
            @PathVariable Long checklistId,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateChecklistItemRequest request) {
        ChecklistItemDTO item = projectTaskService.updateChecklistItemName(taskId, checklistId, itemId, request);
        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/tasks/{taskId}/RemoveChecklistItem/{checklistId}/items/{itemId}")
    public ResponseEntity<TaskCreationResponseDTO> removeChecklistItem(
            @PathVariable Long taskId,
            @PathVariable Long checklistId,
            @PathVariable Long itemId) {
        TaskCreationResponseDTO response = projectTaskService.removeChecklistItem(taskId, checklistId, itemId);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/tasks/{taskId}/DeleteChecklist/{checklistId}")
    public ResponseEntity<TaskCreationResponseDTO> deleteChecklist(
            @PathVariable Long taskId,
            @PathVariable Long checklistId) {
        TaskCreationResponseDTO response = projectTaskService.deleteChecklist(taskId, checklistId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/tasks/{taskId}/ToggleChecklistItemStatus/{checklistId}/items/{itemId}/status")
    public ResponseEntity<ChecklistItemDTO> toggleChecklistItemStatus(
            @PathVariable Long taskId,
            @PathVariable Long checklistId,
            @PathVariable Long itemId,
            @Valid @RequestBody ToggleItemStatusRequest request) {
        ChecklistItemDTO item = projectTaskService.toggleChecklistItemStatus(taskId, checklistId, itemId, request);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/tasks/{taskId}/GetAllChecklists")
    public ResponseEntity<List<ChecklistDTO>> getAllChecklists(
            @PathVariable Long taskId) {
        List<ChecklistDTO> checklists = projectTaskService.getAllChecklists(taskId);
        return ResponseEntity.ok(checklists);
    }



    @PostMapping("/tasks/{taskId}/addComment")
    public ResponseEntity<CommentDTO> addComment(
            @PathVariable Long taskId,
            @Valid @RequestBody CreateCommentRequest request) {
        CommentDTO comment = projectTaskService.addComment(taskId, request);
        return ResponseEntity.ok(comment);
    }

        @PutMapping("/tasks/{taskId}/updateComment/{commentId}")
    public ResponseEntity<CommentDTO> updateComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @Valid @RequestBody UpdateCommentRequest request) {
        CommentDTO comment = projectTaskService.updateComment(taskId, commentId, request);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/tasks/{taskId}/getAllComments")
    public ResponseEntity<List<CommentDTO>> getAllComments(
            @PathVariable Long taskId) {
        List<CommentDTO> comments = projectTaskService.getAllComments(taskId);
        return ResponseEntity.ok(comments);
    }

    @DeleteMapping("/tasks/{taskId}/deleteComment/{commentId}")
    public ResponseEntity<TaskCreationResponseDTO> deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId) {
        TaskCreationResponseDTO response = projectTaskService.deleteComment(taskId, commentId);
        return ResponseEntity.ok(response);
    }


    @PostMapping(value = "/tasks/{taskId}/uploadTaskAttachment", consumes = "multipart/form-data")
    public ResponseEntity<CreateAttachmentResponseDTO> uploadTaskAttachment(
            @PathVariable Long taskId,
            @RequestParam("file") MultipartFile file) {
        try {
            CreateAttachmentResponseDTO response = projectTaskService.addAttachment(taskId, file);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error uploading attachment: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }

    @DeleteMapping(value = "/tasks/{taskId}/deleteTaskAttachment/{attachmentId}")
    public ResponseEntity<String> deleteTaskAttachment(
            @PathVariable Long taskId,
            @PathVariable Long attachmentId) {
        try {
            projectTaskService.removeAttachment(taskId, attachmentId); // Call the service method to delete the attachment
            return ResponseEntity.ok("Attachment removed successfully");
        } catch (Exception e) {
            System.err.println("Error removing attachment: " + e.getMessage());
            return ResponseEntity.status(500).body("Failed to remove attachment: " + e.getMessage());
        }
    }

    @PostMapping("/tasks/{taskId}/UpdateCover")
    public ResponseEntity<UpdateTaskCoverResponseDTO> updateTaskCover(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskCoverRequestDTO request) {
        try {
            UpdateTaskCoverResponseDTO response = projectTaskService.updateTaskCover(taskId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error updating task cover: " + e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
    @PostMapping("/tasks/{taskId}/setTaskDates")
    public ResponseEntity<TaskDatesResponseDTO> setTaskDates(
            @PathVariable Long taskId,
            @RequestBody SetTaskDatesRequestDTO request) {
        try {
            TaskDatesResponseDTO response = projectTaskService.setTaskDates(taskId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error setting task dates: " + e.getMessage());
            return ResponseEntity.status(400).body(TaskDatesResponseDTO.builder()
                    .taskId(taskId)
                    .message("Error: " + e.getMessage())
                    .build());
        }
    }

    @DeleteMapping("/tasks/{taskId}/resetTaskDates")
    public ResponseEntity<TaskDatesResponseDTO> resetTaskDates(
            @PathVariable Long taskId,
            @RequestBody Set<String> fields) {
        try {
            TaskDatesResponseDTO response = projectTaskService.resetTaskDates(taskId, fields);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error resetting task dates: " + e.getMessage());
            return ResponseEntity.status(400).body(TaskDatesResponseDTO.builder()
                    .taskId(taskId)
                    .message("Error: " + e.getMessage())
                    .build());
        }
    }

    @PutMapping("/tasks/{taskId}/updateTaskDates")
    public ResponseEntity<TaskDatesResponseDTO> updateTaskDates(
            @PathVariable Long taskId,
            @RequestBody UpdateTaskDatesRequestDTO request) {
        try {
            TaskDatesResponseDTO response = projectTaskService.updateTaskDates(taskId, request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.err.println("Error updating task dates: " + e.getMessage());
            return ResponseEntity.status(400).body(TaskDatesResponseDTO.builder()
                    .taskId(taskId)
                    .message("Error: " + e.getMessage())
                    .build());
        }
    }
}
