package com.midou.tutorial.Projects.repositories;

import com.midou.tutorial.Projects.entities.Project;
import com.midou.tutorial.Projects.entities.ProjectCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<ProjectCard> findProjectCardById(Long id);
}
