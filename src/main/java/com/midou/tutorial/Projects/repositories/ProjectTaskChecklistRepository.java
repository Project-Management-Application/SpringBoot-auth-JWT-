package com.midou.tutorial.Projects.repositories;

import com.midou.tutorial.Projects.entities.ProjectTaskChecklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskChecklistRepository extends JpaRepository<ProjectTaskChecklist, Long> {
    List<ProjectTaskChecklist> findByTaskId(Long taskId);
}