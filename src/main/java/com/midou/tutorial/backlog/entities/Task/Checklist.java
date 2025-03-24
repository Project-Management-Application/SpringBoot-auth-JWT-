package com.midou.tutorial.backlog.entities.Task;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Checklist {
    @Id
    @SequenceGenerator(
            name = "checklist_sequence",
            sequenceName = "checklist_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "checklist_sequence"
    )
    private long checklistId;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChecklistItem> checklistItems;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
}
