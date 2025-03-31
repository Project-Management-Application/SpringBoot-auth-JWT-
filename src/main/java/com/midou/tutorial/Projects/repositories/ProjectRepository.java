package com.midou.tutorial.Projects.repositories;



import com.midou.tutorial.Projects.entities.Project;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProjectRepository extends JpaRepository<Project, Long> {
}