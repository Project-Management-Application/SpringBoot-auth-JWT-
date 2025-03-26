package com.midou.tutorial.backlog.repositories;

import com.midou.tutorial.backlog.entities.task.Checklist;
import org.springframework.data.repository.CrudRepository;

public interface ChecklistRepository  extends CrudRepository<Checklist, Long> {
}
