package com.midou.tutorial.backlog.entities.task;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChecklistItem {
    @Id
    @SequenceGenerator(
            name = "checklistItem_sequence",
            sequenceName = "checklistItem_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "checklistItem_sequence"
    )
    private long checklistItemId;

    private String title;

    @ManyToOne
    @JoinColumn(name = "checklist_id", nullable = false)
    private Checklist checklist;
}
