package com.midou.tutorial.backlog.entities;

import com.midou.tutorial.backlog.entities.task.Task;
import com.midou.tutorial.backlog.entities.task.TaskContainer;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sprint")
public class Sprint implements TaskContainer {
    @Id
    @SequenceGenerator(
            name = "sprint_sequence",
            sequenceName = "sprint_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator =  "sprint_sequence"
    )
    private long sprintId;

    private String title;

    @Column(nullable = false)
    private Boolean started = false;

    @Column(nullable = false)
    private Boolean completed = false;

    @ManyToOne
    @JoinColumn(name = "backlog_id", nullable = false)
    private Backlog backlog;

    @OneToMany(mappedBy = "sprint", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @Override
    public void removeTask(Task task) {
        tasks.remove(task);
    }

    @Override
    public void addTask(Task task) {
        tasks.add(task);
    }

    @Override
    public boolean containsTask(Task task) {
        if (task == null || tasks == null) {
            return false;
        }
        return tasks.contains(task);
    }
}
