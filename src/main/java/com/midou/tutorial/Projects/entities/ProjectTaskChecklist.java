package com.midou.tutorial.Projects.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ProjectTask_checklists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProjectTaskChecklist { // Renamed class
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
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "task_id", nullable = false)
    private ProjectTask task;

    @OneToMany(mappedBy = "checklist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectCheckListItem> items = new ArrayList<>();
}