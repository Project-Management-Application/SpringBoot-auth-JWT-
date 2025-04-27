package com.midou.tutorial.Projects.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.midou.tutorial.Projects.enums.TaskStatus;
import com.midou.tutorial.user.entities.User;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "project_tasks")
public class ProjectTask {
    @Id
    @SequenceGenerator(
            name = "project_task_sequence",
            sequenceName = "project_task_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "project_task_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column
    private String coverImage; // URL for image cover

    @Column
    private String coverColor; // Color code for cover (e.g., #FF0000 for red)

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime dueDate;

    @Column
    private LocalDateTime dueDateReminder; // Reminder time for due date

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status; // Enum: DONE, PENDING

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private ProjectCard card;

    @ManyToMany
    @JoinTable(
            name = "task_assigned_members",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> assignedMembers = new ArrayList<>();
    @JsonManagedReference
    @ManyToMany
    @JoinTable(
            name = "task_labels",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id")
    )
    private List<ProjectTaskLabel> projectTaskLabels = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTaskComment> projectTaskComments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTaskAttachment> projectTaskAttachments = new ArrayList<>();

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectTaskChecklist> checklists = new ArrayList<>();
}
