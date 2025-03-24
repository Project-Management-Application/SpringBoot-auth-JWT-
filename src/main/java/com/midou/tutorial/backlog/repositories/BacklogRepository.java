package com.midou.tutorial.backlog.repositories;

import com.midou.tutorial.backlog.entities.Backlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BacklogRepository  extends JpaRepository<Backlog, Long> {
}
