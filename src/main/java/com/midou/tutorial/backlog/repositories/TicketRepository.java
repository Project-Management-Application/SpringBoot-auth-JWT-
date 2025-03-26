package com.midou.tutorial.backlog.repositories;

import com.midou.tutorial.backlog.entities.task.Ticket;
import org.springframework.data.repository.CrudRepository;

public interface TicketRepository extends CrudRepository<Ticket, Long> {
}
