package com.midou.tutorial.Projects.entities;

import com.midou.tutorial.Projects.enums.ProjectRole;
import com.midou.tutorial.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "project_invitations")
public class ProjectInvitation {
    @Id
    @SequenceGenerator(
            name = "project_invitation_sequence",
            sequenceName = "project_invitation_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_invitation_sequence"
    )
    private Long id;

    @ManyToOne
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;

    @ManyToOne
    @JoinColumn(name = "invited_user_id", nullable = false)
    private User invitedUser;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectRole role;

    @Column(nullable = false)
    private LocalDateTime expiresAt;
}
