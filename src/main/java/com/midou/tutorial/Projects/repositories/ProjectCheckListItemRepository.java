package com.midou.tutorial.Projects.repositories;

import com.midou.tutorial.Projects.entities.ProjectCheckListItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjectCheckListItemRepository extends JpaRepository<ProjectCheckListItem, Long> {
}
