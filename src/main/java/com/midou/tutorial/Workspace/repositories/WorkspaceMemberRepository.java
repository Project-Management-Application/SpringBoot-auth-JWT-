package com.midou.tutorial.Workspace.repositories;

import com.midou.tutorial.Workspace.entities.WorkspaceMember;
import com.midou.tutorial.Workspace.entities.WorkspaceMemberId;
import com.midou.tutorial.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, WorkspaceMemberId> {
    List<WorkspaceMember> findByUser(User user);
}