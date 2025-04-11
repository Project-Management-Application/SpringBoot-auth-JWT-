package com.midou.tutorial.Projects.repositories;

import com.midou.tutorial.Projects.DTO.ProjectInvitationProjection;
import com.midou.tutorial.Projects.entities.ProjectInvitation;
import com.midou.tutorial.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation, Long> {

    @Query("SELECT pi.id as id, pi.project.name as projectName, pi.expiresAt as expiresAt " +
            "FROM ProjectInvitation pi " +
            "WHERE pi.invitedUser = :user AND pi.expiresAt > CURRENT_TIMESTAMP")
    List<ProjectInvitationProjection> findPendingInvitationsByUser(User user);
}
