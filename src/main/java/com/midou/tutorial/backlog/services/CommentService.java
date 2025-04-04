package com.midou.tutorial.backlog.services;

import com.midou.tutorial.backlog.dto.CreateCommentDTO;
import com.midou.tutorial.backlog.entities.task.Checklist;
import com.midou.tutorial.backlog.entities.task.ChecklistItem;
import com.midou.tutorial.backlog.entities.task.Comment;
import com.midou.tutorial.backlog.entities.task.CommentSection;
import com.midou.tutorial.backlog.repositories.ChecklistRepository;
import com.midou.tutorial.backlog.repositories.CommentRepository;
import com.midou.tutorial.backlog.repositories.CommentSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final CommentSectionRepository commentSectionRepository;


    public long createComment(CreateCommentDTO comment) {
        CommentSection commentSection = commentSectionRepository.findById(comment.getCommentSectionId()).orElseThrow(() -> new RuntimeException("comment section not found"));
        var comment1 = Comment.builder()
                .commentSection(commentSection)
                .comment(comment.getComment())
                .build();
        return commentRepository.save(comment1).getCommentId();
    }
}
