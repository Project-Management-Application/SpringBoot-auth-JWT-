package com.midou.tutorial.Projects.repositories;
import com.midou.tutorial.Projects.entities.ProjectMember;
import com.midou.tutorial.Projects.entities.ProjectMemberId;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ProjectMemberRepository extends JpaRepository<ProjectMember, ProjectMemberId> {

}
