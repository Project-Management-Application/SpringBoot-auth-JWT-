package com.midou.tutorial.backlog.repositories;

import com.midou.tutorial.backlog.entities.task.ChecklistItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChecklistItemRepository extends JpaRepository<ChecklistItem, Long> {
}
