package com.midou.tutorial.backlog.controllers;

import com.midou.tutorial.backlog.dto.CreateCommentDTO;
import com.midou.tutorial.backlog.dto.checklistDTO.CreateChecklistDTO;
import com.midou.tutorial.backlog.services.BacklogService;
import com.midou.tutorial.backlog.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;


    @PostMapping("/createComment")
    public long createComment(@RequestBody CreateCommentDTO comment) {
        return commentService.createComment(comment);
    }
}
