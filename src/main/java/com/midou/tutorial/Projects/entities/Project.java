package com.midou.tutorial.Projects.entities;

import com.midou.tutorial.Models.entities.Model;
import com.midou.tutorial.backlog.entities.Backlog;
import com.midou.tutorial.backlog.entities.task.CommentSection;
import com.midou.tutorial.user.entities.User;
import com.midou.tutorial.Workspace.entities.Workspace;
import com.midou.tutorial.Projects.enums.Visibility;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Check;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "projects")
@Check(
        name = "check_background_with_model",
        constraints = "(model_id IS NULL AND (background_image IS NOT NULL OR background_color IS NOT NULL)) OR " +
                "(model_id IS NOT NULL AND background_image IS NULL AND background_color IS NULL)"
)
public class Project {
    @Id
    @SequenceGenerator(
            name = "project_sequence",
            sequenceName = "project_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Enumerated(EnumType.STRING)
    @Column
    private Visibility visibility;
    @Column
    private String backgroundImage; // URL of the selected background image

    @Column
    private String backgroundColor;
    @ManyToOne
    @JoinColumn(name = "workspace_id", nullable = false)
    private Workspace workspace;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model; // Optional template used

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectCard> cards;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectMember> members = new ArrayList<>();


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "backlog_id", referencedColumnName = "backlogId")
    private Backlog backlog;
}