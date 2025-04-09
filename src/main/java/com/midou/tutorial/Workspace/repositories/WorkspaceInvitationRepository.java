package com.midou.tutorial.Workspace.repositories;



import com.midou.tutorial.Workspace.DTO.WorkspaceInvitationProjection;
import com.midou.tutorial.Workspace.entities.WorkspaceInvitation;
import com.midou.tutorial.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WorkspaceInvitationRepository extends JpaRepository<WorkspaceInvitation, Long> {
    @Query("SELECT wi.id AS id, w.name AS workspaceName, wi.expiresAt AS expiresAt " +
            "FROM WorkspaceInvitation wi " +
            "JOIN wi.workspace w " +
            "WHERE wi.invitedUser = :user AND wi.accepted = false")
    List<WorkspaceInvitationProjection> findPendingInvitationsByUser(@Param("user") User user);
}