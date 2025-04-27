package com.midou.tutorial.Projects.entities;

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
@Table(name = "ProjectTask_comments")
public class ProjectTaskComment {
    @Id
    @SequenceGenerator(
            name = "comment_sequence",
            sequenceName = "comment_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "comment_sequence"
    )
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content; // Supports plain text, @mentions, links, emojis

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private ProjectTask task;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User author;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;
}