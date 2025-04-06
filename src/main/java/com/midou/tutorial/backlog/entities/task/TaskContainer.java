package com.midou.tutorial.backlog.entities.task;

public interface TaskContainer {
    void removeTask(Task task);
    void addTask(Task task);
    boolean containsTask(Task task);
}
