package com.midou.tutorial.Projects.entities;

import com.midou.tutorial.user.entities.User;
import com.midou.tutorial.Projects.enums.ProjectRole;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "project_members")
public class ProjectMember {
    @EmbeddedId
    private ProjectMemberId id;

    @ManyToOne
    @MapsId("projectId")
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    public void setProject(Project project) {
        this.project = project;
        updateId();
    }

    public void setUser(User user) {
        this.user = user;
        updateId();
    }
    private void updateId() {
        if (project != null && user != null) {
            this.id = new ProjectMemberId(project.getId(), user.getId());
        }
    }


    @PrePersist
    public void prePersist() {
        if (id == null && project != null && user != null) {
            this.id = new ProjectMemberId(project.getId(), user.getId());
        }
    }
}