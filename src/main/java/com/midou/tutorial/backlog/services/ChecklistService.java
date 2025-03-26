package com.midou.tutorial.backlog.services;

import com.midou.tutorial.backlog.dto.createChecklistDTO;
import com.midou.tutorial.backlog.dto.createChecklistItemDTO;
import com.midou.tutorial.backlog.dto.updateChecklistItemTitleDTO;
import com.midou.tutorial.backlog.dto.updateChecklistTitleDTO;
import com.midou.tutorial.backlog.entities.task.Checklist;
import com.midou.tutorial.backlog.entities.task.ChecklistItem;
import com.midou.tutorial.backlog.entities.task.Task;
import com.midou.tutorial.backlog.repositories.ChecklistItemRepository;
import com.midou.tutorial.backlog.repositories.ChecklistRepository;
import com.midou.tutorial.backlog.repositories.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChecklistService {
    private final ChecklistRepository checklistRepository;
    private final ChecklistItemRepository checklistItemRepository;

    private final TaskRepository taskRepository;

    public long createChecklist(createChecklistDTO checklist) {
        Task task = taskRepository.findById(checklist.getTaskId()).orElseThrow(() -> new RuntimeException("task not found"));
        var checklist1 = Checklist.builder()
                .task(task)
                .title(checklist.getTitle())
                .build();
        return checklistRepository.save(checklist1).getChecklistId();
    }

    public long deleteChecklist(long checklistId) {
        Checklist checklist = checklistRepository.findById(checklistId).orElseThrow(() -> new RuntimeException("checklist not found"));
        checklistRepository.delete(checklist);
        return checklist.getChecklistId();
    }

    public long updateChecklistTitle(updateChecklistTitleDTO checklist) {
        Checklist checklist1 = checklistRepository.findById(checklist.getChecklistId()).orElseThrow(() -> new RuntimeException("checklist not found"));
        checklist1.setTitle(checklist.getTitle());
        return checklistRepository.save(checklist1).getChecklistId();
    }

    public long createChecklistItem(createChecklistItemDTO checklistItem) {
        Checklist checklist = checklistRepository.findById(checklistItem.getChecklistId()).orElseThrow(() -> new RuntimeException("checklist not found"));
        var checklistItem1 = ChecklistItem.builder()
                .checklist(checklist)
                .title(checklistItem.getTitle())
                .build();
        return checklistItemRepository.save(checklistItem1).getChecklistItemId();
    }

    public long deleteChecklistItem(long checklistItemId) {
        ChecklistItem checklistItem = checklistItemRepository.findById(checklistItemId).orElseThrow(() -> new RuntimeException("checklist Item not found"));
        checklistItemRepository.delete(checklistItem);
        return checklistItem.getChecklistItemId();
    }

    public long updateChecklistItemTitle(updateChecklistItemTitleDTO checklistItem) {
        ChecklistItem checklistItem1 = checklistItemRepository.findById(checklistItem.getChecklistItemId()).orElseThrow(() -> new RuntimeException("checklist Item not found"));
        checklistItem1.setTitle(checklistItem.getTitle());
        return checklistItemRepository.save(checklistItem1).getChecklistItemId();
    }

    public String checkItem(long checklistItemId) {
        ChecklistItem checklistItem = checklistItemRepository.findById(checklistItemId).orElseThrow(() -> new RuntimeException("checklist Item not found"));
        String response;
        if (!checklistItem.isChecked()) {
            checklistItem.setChecked(true);
            response = "checked";
        } else {
            checklistItem.setChecked(false);
            response = "unchecked";
        }
        checklistItemRepository.save(checklistItem);
        return response;
    }
}
