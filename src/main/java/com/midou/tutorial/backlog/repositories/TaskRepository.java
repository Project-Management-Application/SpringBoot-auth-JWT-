package com.midou.tutorial.backlog.repositories;

import com.midou.tutorial.backlog.entities.Task.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
