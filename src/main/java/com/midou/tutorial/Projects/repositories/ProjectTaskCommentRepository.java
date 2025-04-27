package com.midou.tutorial.Projects.repositories;


import com.midou.tutorial.Projects.entities.ProjectTaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskCommentRepository extends JpaRepository<ProjectTaskComment, Long> {
    List<ProjectTaskComment> findByTaskId(Long taskId);
}
