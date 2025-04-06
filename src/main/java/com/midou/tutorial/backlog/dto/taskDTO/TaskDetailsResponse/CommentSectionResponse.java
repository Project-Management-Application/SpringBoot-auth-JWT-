package com.midou.tutorial.backlog.dto.taskDTO.TaskDetailsResponse;

import java.util.List;

public record CommentSectionResponse(Long commentSectionId, List<CommentResponse> comments) {
}
