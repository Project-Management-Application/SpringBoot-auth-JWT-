package com.midou.tutorial.backlog.controllers;

import com.midou.tutorial.backlog.dto.taskDTO.TaskDetailsResponse.TicketResponse;
import com.midou.tutorial.backlog.dto.ticketDTO.UpdateTicketColorDTO;
import com.midou.tutorial.backlog.dto.ticketDTO.UpdateTicketTitleDTO;
import com.midou.tutorial.backlog.dto.ticketDTO.CreateTicketDTO;
import com.midou.tutorial.backlog.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class TicketController {
    private final TicketService ticketService;

    @PostMapping("/createTicket")
    public long createTicket(@RequestBody CreateTicketDTO ticket){
        return ticketService.createTicket(ticket);
    }

    @PatchMapping("/updateTicketTitle")
    public long updateTicketTitle(@RequestBody UpdateTicketTitleDTO ticket){
        return ticketService.updateTicketTitle(ticket);
    }

    @PatchMapping("/updateTicketColor")
    public long updateTicketColor(@RequestBody UpdateTicketColorDTO ticket){
        return ticketService.updateTicketColor(ticket);
    }

    @DeleteMapping("/deleteTicket/{ticketId}")
    public long deleteTicket(@PathVariable long ticketId){
        return ticketService.deleteTicket(ticketId);
    }

    @GetMapping("/getAllTickets")
    public List<TicketResponse> getAllTickets() {
        return ticketService.getAllTickets();
    }
}
