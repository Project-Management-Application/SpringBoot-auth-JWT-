package com.midou.tutorial.backlog.dto.taskDTO.TaskDetailsResponse;

import com.midou.tutorial.backlog.enums.Label;

import java.util.List;
import java.util.Set;

public record TaskResponse(
        Long taskId,
        String title,
        String description,
        Label label,
        Set<TicketResponse> tickets,
        List<ChecklistResponse> checklists,
        CommentSectionResponse commentSection
) {
}
