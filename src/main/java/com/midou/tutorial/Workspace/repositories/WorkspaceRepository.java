package com.midou.tutorial.Workspace.repositories;

import com.midou.tutorial.Workspace.entities.Workspace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WorkspaceRepository extends JpaRepository<Workspace, Long> {
    boolean existsByOwnerId(Long ownerId);
    Optional<Workspace> findByOwnerId(Long ownerId);
}