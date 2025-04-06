package com.midou.tutorial.backlog.repositories;

import com.midou.tutorial.backlog.entities.task.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
