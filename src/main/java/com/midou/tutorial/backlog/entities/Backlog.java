package com.midou.tutorial.backlog.entities;

import com.midou.tutorial.backlog.entities.task.Task;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "backlog")
public class Backlog {
    @Id
    @SequenceGenerator(
            name = "backlog_sequence",
            sequenceName = "backlog_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "backlog_sequence"
    )
    private long backlogId;

    @OneToMany(mappedBy = "backlog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks;

    @OneToMany(mappedBy = "backlog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sprint> Sprints;

}
