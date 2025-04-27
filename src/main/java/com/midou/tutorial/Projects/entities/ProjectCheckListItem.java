package com.midou.tutorial.Projects.entities;


import com.midou.tutorial.user.entities.User;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ProjectTask_checklist_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectCheckListItem {
    @Id
    @SequenceGenerator(
            name = "projectchecklist_item_sequence",
            sequenceName = "projectchecklist_item_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "projectchecklist_item_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private boolean isCompleted = false;

    @ManyToOne
    @JoinColumn(name = "projectchecklist_id", nullable = false)
    private ProjectTaskChecklist checklist;

    @ManyToOne
    @JoinColumn(name = "assigned_to")
    private User assignedTo;
}