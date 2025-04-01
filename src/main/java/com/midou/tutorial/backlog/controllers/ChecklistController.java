package com.midou.tutorial.backlog.controllers;

import com.midou.tutorial.backlog.dto.checklistDTO.CreateChecklistDTO;
import com.midou.tutorial.backlog.dto.checklistDTO.CreateChecklistItemDTO;
import com.midou.tutorial.backlog.dto.checklistDTO.UpdateChecklistItemTitleDTO;
import com.midou.tutorial.backlog.dto.checklistDTO.UpdateChecklistTitleDTO;
import com.midou.tutorial.backlog.services.ChecklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ChecklistController {
    private final ChecklistService checklistService;

    @PostMapping("/createChecklist")
    public long createChecklist(@RequestBody CreateChecklistDTO checklist) {
        return checklistService.createChecklist(checklist);
    }

    @DeleteMapping("/deleteChecklist/{checklistId}")
    public long deleteChecklist(@PathVariable long checklistId) {
        return checklistService.deleteChecklist(checklistId);
    }

    @PatchMapping("/updateChecklistTitle")
    public long updateChecklistTitle(@RequestBody UpdateChecklistTitleDTO checklist){
        return checklistService.updateChecklistTitle(checklist);
    }

    @PostMapping("/createChecklistItem")
    public long createChecklistItem(@RequestBody CreateChecklistItemDTO checklistItem) {
        return checklistService.createChecklistItem(checklistItem);
    }

    @DeleteMapping("/deleteChecklistItem/{checklistItemId}")
    public long deleteChecklistItem(@PathVariable long checklistItemId) {
        return checklistService.deleteChecklistItem(checklistItemId);
    }

    @PatchMapping("/updateChecklistItemTitle")
    public long updateChecklistItemTitle(@RequestBody UpdateChecklistItemTitleDTO checklistItem){
        return checklistService.updateChecklistItemTitle(checklistItem);
    }

    @PatchMapping("/checkItem/{checklistItemId}")
    public boolean checkItem(@PathVariable long checklistItemId) {
        return checklistService.checkItem(checklistItemId);
    }
}
