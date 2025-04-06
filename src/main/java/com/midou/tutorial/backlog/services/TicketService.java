package com.midou.tutorial.backlog.services;

import com.midou.tutorial.backlog.dto.taskDTO.TaskDetailsResponse.TicketResponse;
import com.midou.tutorial.backlog.dto.ticketDTO.UpdateTicketColorDTO;
import com.midou.tutorial.backlog.dto.ticketDTO.UpdateTicketTitleDTO;
import com.midou.tutorial.backlog.dto.ticketDTO.CreateTicketDTO;
import com.midou.tutorial.backlog.entities.task.Ticket;
import com.midou.tutorial.backlog.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;

    public long createTicket(CreateTicketDTO ticket) {
        var ticket1 = Ticket.builder()
                .title(ticket.getTitle())
                .colorCode(ticket.getColorCode())
                .build();
        ticketRepository.save(ticket1);
        return ticket1.getTicketId();
    }

    public long updateTicketTitle(UpdateTicketTitleDTO ticket) {
        Ticket ticket1 = ticketRepository.findById(ticket.getTicketId()).orElseThrow(() -> new RuntimeException("Task not found"));
        ticket1.setTitle(ticket.getTitle());
        return ticketRepository.save(ticket1).getTicketId();
    }

    public long updateTicketColor(UpdateTicketColorDTO ticket) {
        Ticket ticket1 = ticketRepository.findById(ticket.getTicketId()).orElseThrow(() -> new RuntimeException("Task not found"));
        ticket1.setColorCode(ticket.getColorCode());
        return ticketRepository.save(ticket1).getTicketId();
    }

    public long deleteTicket(long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId).orElseThrow(() -> new RuntimeException("Ticket not found"));
        ticketRepository.delete(ticket);
        return ticket.getTicketId();
    }

    public List<TicketResponse> getAllTickets() {
        return StreamSupport.stream(ticketRepository.findAll().spliterator(), false)
                .map(ticket -> new TicketResponse(
                        ticket.getTicketId(),
                        ticket.getTitle(),
                        ticket.getColorCode()
                ))
                .collect(Collectors.toList());
    }
}
