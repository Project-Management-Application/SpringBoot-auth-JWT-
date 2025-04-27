package com.midou.tutorial.Projects.repositories;

import com.midou.tutorial.Projects.entities.ProjectTaskLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectTaskLabelRepository extends JpaRepository<ProjectTaskLabel, Long> {
    List<ProjectTaskLabel> findByCategoryId(Long categoryId);
    boolean existsByCategoryIdAndTagValue(Long categoryId, String tagValue);
}