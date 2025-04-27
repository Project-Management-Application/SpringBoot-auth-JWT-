package com.midou.tutorial.Projects.entities;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ProjectTasks_label_categories")
public class ProjectTaskLabelCategory {
    @Id
    @SequenceGenerator(
            name = "label_category_sequence",
            sequenceName = "label_category_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "label_category_sequence"
    )
    private Long id;

    @Column(nullable = false)
    private String name; // e.g., Status, Priority, Type

    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ProjectTaskLabel> projectTaskLabels = new ArrayList<>();

    // Flag to indicate if this is a default (system-provided) category
    @Column(nullable = false)
    private boolean isDefault = false;
}
