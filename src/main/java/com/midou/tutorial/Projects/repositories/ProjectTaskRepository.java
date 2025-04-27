package com.midou.tutorial.Projects.repositories;

import com.midou.tutorial.Projects.entities.ProjectTask;
import com.midou.tutorial.Projects.enums.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface ProjectTaskRepository extends JpaRepository<ProjectTask, Long> {
    List<ProjectTask> findByCardId(Long cardId);
    List<ProjectTask> findByCardProjectId(Long projectId);
    List<ProjectTask> findByCardIdAndStatus(Long cardId, TaskStatus status);
}