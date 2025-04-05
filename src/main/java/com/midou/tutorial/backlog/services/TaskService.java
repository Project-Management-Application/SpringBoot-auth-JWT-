package com.midou.tutorial.backlog.services;

import com.midou.tutorial.backlog.dto.taskDTO.*;
import com.midou.tutorial.backlog.dto.taskDTO.TaskDetailsResponse.*;
import com.midou.tutorial.backlog.entities.Backlog;
import com.midou.tutorial.backlog.entities.Sprint;
import com.midou.tutorial.backlog.entities.task.TaskContainer;
import com.midou.tutorial.backlog.entities.task.CommentSection;
import com.midou.tutorial.backlog.entities.task.Task;
import com.midou.tutorial.backlog.entities.task.Ticket;
import com.midou.tutorial.backlog.enums.Label;
import com.midou.tutorial.backlog.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

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
        task1.setCommentSection(commentSection1);
        taskRepository.save(task1);
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

    public TaskResponse getTaskDetails(Long taskId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new RuntimeException("Task not found"));
        return new TaskResponse(
                task.getTaskId(),
                task.getTitle(),
                task.getDescription(),
                task.getLabel(),
                task.getTickets().stream()
                        .map(ticket -> new TicketResponse(ticket.getTicketId(), ticket.getTitle(), ticket.getColorCode()))
                        .collect(Collectors.toSet()),
                task.getChecklists().stream()
                        .map(checklist -> new ChecklistResponse(
                                checklist.getChecklistId(),
                                checklist.getTitle(),
                                checklist.getChecklistItems().stream()
                                        .map(checklistItem -> new ChecklistItemResponse(checklistItem.getChecklistItemId(),checklistItem.getTitle(), checklistItem.isChecked()))
                                        .collect(Collectors.toList())))
                        .collect(Collectors.toList()),
                task.getCommentSection() != null ?
                        new CommentSectionResponse(
                                task.getCommentSection().getCommentSectionId(),
                                task.getCommentSection().getComments().stream()
                                        .map(comment -> new CommentResponse(comment.getCommentId(), comment.getComment()))
                                        .collect(Collectors.toList())
                        ) : null

        );
    }
    @Transactional
    public void moveTask(moveTaskDTO request) {
        Task task = taskRepository.findById(request.getTaskId()).orElseThrow(() -> new RuntimeException("Task not found"));
        TaskContainer sender = getContainer(request.getSenderType(), request.getSenderId());
        TaskContainer receiver = getContainer(request.getReceiverType(), request.getReceiverId());

        if (!sender.containsTask(task)) {
            throw new RuntimeException("Task not found in sender container");
        }


        if (sender instanceof Sprint) {
            if(((Sprint) sender).getStarted() || ((Sprint) sender).getCompleted()){
                throw new RuntimeException("sender Sprint already started or already finished");
            }
            sender.removeTask(task);
            sprintRepository.save((Sprint) sender);
            task.setSprint(null);
        } else {
            backlogRepository.save((Backlog) sender);
            task.setBacklog(null);
        }

        if (receiver instanceof Sprint) {
            if(((Sprint) receiver).getStarted() || ((Sprint) receiver).getCompleted()){
                throw new RuntimeException("receiver Sprint already started or already finished");
            }
            receiver.addTask(task);
            sprintRepository.save((Sprint) receiver);
            task.setSprint((Sprint) receiver);
        } else {
            backlogRepository.save((Backlog) receiver);
            task.setBacklog((Backlog) receiver);
        }
        taskRepository.save(task);
    }


    private TaskContainer getContainer(String type , Long id){
        if(type.equalsIgnoreCase("sprint")){
            return (TaskContainer)sprintRepository.findById(id).orElseThrow(() -> new RuntimeException("sprint not found"));
        }else if(type.equalsIgnoreCase("backlog")){
            return (TaskContainer)backlogRepository.findById(id).orElseThrow(() -> new RuntimeException("backlog not found"));
        }else {
            throw new RuntimeException("Invalid container type");
        }
    }
}
