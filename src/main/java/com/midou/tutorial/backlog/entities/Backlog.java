package com.midou.tutorial.backlog.entities;

import com.midou.tutorial.Projects.entities.Project;
import com.midou.tutorial.backlog.entities.task.Task;
import com.midou.tutorial.backlog.entities.task.TaskContainer;
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
public class Backlog implements TaskContainer {
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

    @OneToMany(mappedBy = "backlog", cascade = CascadeType.ALL)
    private List<Task> tasks;

    @OneToMany(mappedBy = "backlog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Sprint> Sprints;


    @OneToOne(mappedBy = "backlog")
    private Project project;

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
