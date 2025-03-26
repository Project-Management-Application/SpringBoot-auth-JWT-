package com.midou.tutorial.backlog.controllers;

import com.midou.tutorial.backlog.dto.checklistDTO.createChecklistDTO;
import com.midou.tutorial.backlog.dto.checklistDTO.createChecklistItemDTO;
import com.midou.tutorial.backlog.dto.checklistDTO.updateChecklistItemTitleDTO;
import com.midou.tutorial.backlog.dto.checklistDTO.updateChecklistTitleDTO;
import com.midou.tutorial.backlog.services.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChecklistController {
    private final ChecklistService checklistService;

    @PostMapping("/createChecklist")
    public long createChecklist(@RequestBody createChecklistDTO checklist) {
        return checklistService.createChecklist(checklist);
    }

    @DeleteMapping("/deleteChecklist")
    public long deleteChecklist(@RequestBody long checklistId) {
        return checklistService.deleteChecklist(checklistId);
    }

    @PatchMapping("/updateChecklistTitle")
    public long updateChecklistTitle(@RequestBody updateChecklistTitleDTO checklist){
        return checklistService.updateChecklistTitle(checklist);
    }

    @PostMapping("/createChecklistItem")
    public long createChecklistItem(@RequestBody createChecklistItemDTO checklistItem) {
        return checklistService.createChecklistItem(checklistItem);
    }

    @DeleteMapping("/deleteChecklistItem")
    public long deleteChecklistItem(@RequestBody long checklistItemId) {
        return checklistService.deleteChecklistItem(checklistItemId);
    }

    @PatchMapping("/updateChecklistItemTitle")
    public long updateChecklistItemTitle(@RequestBody updateChecklistItemTitleDTO checklistItem){
        return checklistService.updateChecklistItemTitle(checklistItem);
    }

    @PatchMapping("/checkItem")
    public String checkItem(@RequestBody long checklistItemId) {
        return checklistService.checkItem(checklistItemId);
    }
}
