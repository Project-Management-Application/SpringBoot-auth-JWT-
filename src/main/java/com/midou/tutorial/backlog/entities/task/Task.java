package com.midou.tutorial.backlog.entities.task;

import com.midou.tutorial.backlog.entities.Backlog;
import com.midou.tutorial.backlog.entities.Sprint;
import com.midou.tutorial.backlog.enums.Label;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {
    @Id
    @SequenceGenerator(
            name = "task_sequence",
            sequenceName = "task_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "task_sequence"
    )
    private long taskId;

    @Column(nullable=false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Label label;

    @Column(nullable=true)
    private String description;

    @ManyToMany
    @JoinTable(
            name = "task_tickets",
            joinColumns = @JoinColumn(name = "task_id"),
            inverseJoinColumns = @JoinColumn(name = "ticket_id")
    )
    private Set<Ticket> tickets = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "backlog_id", nullable = true)
    private Backlog backlog;

    @ManyToOne
    @JoinColumn(name = "sprint_id", nullable = true)
    private Sprint sprint;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Checklist> checklists ;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "commentSection_id", referencedColumnName = "commentSectionId")
    private CommentSection commentSection;

}
