package com.midou.tutorial.Workspace.repositories;

import com.midou.tutorial.Workspace.entities.WorkspaceMember;
import com.midou.tutorial.Workspace.entities.WorkspaceMemberId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WorkspaceMemberRepository extends JpaRepository<WorkspaceMember, WorkspaceMemberId> {

}