package com.midou.tutorial.backlog.services;

import com.midou.tutorial.backlog.dto.ticketDTO.UpdateTicketColorDTO;
import com.midou.tutorial.backlog.dto.ticketDTO.UpdateTicketTitleDTO;
import com.midou.tutorial.backlog.dto.taskDTO.CreateTicketDTO;
import com.midou.tutorial.backlog.entities.task.Ticket;
import com.midou.tutorial.backlog.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
