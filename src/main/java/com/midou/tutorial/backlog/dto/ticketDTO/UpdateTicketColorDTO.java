package com.midou.tutorial.backlog.dto.ticketDTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateTicketColorDTO {
    private long ticketId;
    private String colorCode;
}
