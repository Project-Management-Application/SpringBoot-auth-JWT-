package com.midou.tutorial.Workspace.entities;



import com.midou.tutorial.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "workspace_members")
@IdClass(WorkspaceMemberId.class)
public class WorkspaceMember {
    @Id
    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}