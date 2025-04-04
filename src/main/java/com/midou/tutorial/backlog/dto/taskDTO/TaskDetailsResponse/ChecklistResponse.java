package com.midou.tutorial.backlog.dto.taskDTO.TaskDetailsResponse;

import java.util.List;

public record ChecklistResponse(Long checklistId, String title, List<ChecklistItemResponse> checklistItems) {}
