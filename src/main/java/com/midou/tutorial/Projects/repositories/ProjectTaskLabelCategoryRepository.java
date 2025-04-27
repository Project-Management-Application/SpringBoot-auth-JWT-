package com.midou.tutorial.Projects.repositories;

import com.midou.tutorial.Projects.entities.ProjectTaskLabelCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjectTaskLabelCategoryRepository extends JpaRepository<ProjectTaskLabelCategory, Long> {
    Optional<ProjectTaskLabelCategory> findByName(String name);
    boolean existsByName(String name);
}