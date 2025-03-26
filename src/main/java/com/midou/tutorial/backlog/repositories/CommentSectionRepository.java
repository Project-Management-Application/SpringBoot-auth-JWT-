package com.midou.tutorial.backlog.repositories;

import com.midou.tutorial.backlog.entities.task.CommentSection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentSectionRepository extends JpaRepository<CommentSection, Long> {
}
