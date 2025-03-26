package com.midou.tutorial.backlog.controllers;

import com.midou.tutorial.backlog.dto.ticketDTO.UpdateTicketColorDTO;
import com.midou.tutorial.backlog.dto.ticketDTO.UpdateTicketTitleDTO;
import com.midou.tutorial.backlog.dto.taskDTO.CreateTicketDTO;
import com.midou.tutorial.backlog.services.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
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
    public long updateTicketTitle(@RequestBody UpdateTicketColorDTO ticket){
        return ticketService.updateTicketColor(ticket);
    }

    @DeleteMapping("/deleteTicket")
    public long deleteTicket(@RequestBody long ticketId){
        return ticketService.deleteTicket(ticketId);
    }

}
