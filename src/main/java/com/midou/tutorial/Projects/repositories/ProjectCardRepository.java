package com.midou.tutorial.Projects.repositories;

import com.midou.tutorial.Projects.entities.Project;
import com.midou.tutorial.Projects.entities.ProjectCard;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectCardRepository extends JpaRepository<ProjectCard, Long> {
    List<ProjectCard> findByProject(Project project);

}
