package com.midou.tutorial.Projects.entities;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ProjectTask_labels")
public class ProjectTaskLabel {
    @Id
    @SequenceGenerator(
            name = "label_sequence",
            sequenceName = "label_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "label_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String tagValue; // e.g., In Progress, High, Bug

    @Column
    private String color; // Color code (e.g., #0000FF for blue)

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private ProjectTaskLabelCategory category;

    // Flag to indicate if this is a default (system-provided) label
    @Column(nullable = false)
    private boolean isDefault = false;
}