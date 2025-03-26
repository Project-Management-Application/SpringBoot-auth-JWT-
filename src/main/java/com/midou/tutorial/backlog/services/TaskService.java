package com.midou.tutorial.backlog.services;

import com.midou.tutorial.backlog.dto.taskDTO.*;
import com.midou.tutorial.backlog.entities.Backlog;
import com.midou.tutorial.backlog.entities.Sprint;
import com.midou.tutorial.backlog.entities.task.CommentSection;
import com.midou.tutorial.backlog.entities.task.Task;
import com.midou.tutorial.backlog.entities.task.Ticket;
import com.midou.tutorial.backlog.enums.Label;
import com.midou.tutorial.backlog.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final BacklogRepository backlogRepository;
    private final SprintRepository sprintRepository;
    private final TaskRepository taskRepository;
    private final CommentSectionRepository commentSectionRepository;
    private final TicketRepository ticketRepository;

    public long createTask(CreateTaskDTO task) {
        Backlog backlog = backlogRepository.findById(task.getBacklogId()).orElse(null);
        Sprint sprint = sprintRepository.findById(task.getSprintId()).orElse(null);
        boolean a = sprint != null;
        boolean b = backlog != null;
        if(a == b) {
            throw new RuntimeException("task can only be linked to either sprint or backlog ");
        }
        var task1 = Task.builder()
                .title(task.getTitle())
                .label(Label.valueOf(task.getLabel()))
                .backlog(backlog)
                .sprint(sprint)
                .build();
        taskRepository.save(task1);

        var commentSection1 = CommentSection.builder()
                .task(task1)
                .build();
        commentSectionRepository.save(commentSection1);
        return task1.getTaskId();
    }

    public long updateTaskTitle(UpdateTaskTitleDTO task) {
        Task task1 = taskRepository.findById(task.getTaskId()).orElseThrow(() -> new RuntimeException("Task not found"));
        task1.setTitle(task.getTitle());
        return taskRepository.save(task1).getTaskId();
    }

    public long updateTaskLabel(UpdateTaskLabelDTO task) {
        Task task1 = taskRepository.findById(task.getTaskId()).orElseThrow(() -> new RuntimeException("Task not found"));
        task1.setLabel(Label.valueOf(task.getLabel()));
        return taskRepository.save(task1).getTaskId();
    }

    public long updateTaskDescription(UpdateTaskDescriptionDTO task) {
        Task task1 = taskRepository.findById(task.getTaskId()).orElseThrow(() -> new RuntimeException("Task not found"));
        task1.setDescription(task.getDescription());
        return taskRepository.save(task1).getTaskId();
    }

    public long deleteTask(long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
        return task.getTaskId();
    }

    public long addTicketToTask(TicketTaskDTO taskTicket) {
        Task task= taskRepository.findById(taskTicket.getTaskId()).orElseThrow(() -> new RuntimeException("Task not found"));
        Ticket ticket = ticketRepository.findById(taskTicket.getTicketId()).orElseThrow(() -> new RuntimeException("Ticket not found"));
        if (!task.getTickets().contains(ticket)) {
            task.getTickets().add(ticket);
            ticket.getTasks().add(task);
        }
        return taskRepository.save(task).getTaskId();
    }

    public long removeTicketFromTask(TicketTaskDTO taskTicket) {
        Task task= taskRepository.findById(taskTicket.getTaskId()).orElseThrow(() -> new RuntimeException("Task not found"));
        Ticket ticket = ticketRepository.findById(taskTicket.getTicketId()).orElseThrow(() -> new RuntimeException("Ticket not found"));
        task.getTickets().remove(ticket);
        ticket.getTasks().remove(task);
        return taskRepository.save(task).getTaskId();
    }

}
