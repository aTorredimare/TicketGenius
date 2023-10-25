package it.polito.waii.ticketingserver.ticket.dtos

import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import java.sql.Timestamp

data class CurrentStatusDTO(
    val status: ETicketStatus,
    val timestamp: Timestamp
)

fun TicketStatusHistoryDTO.toDTO(): CurrentStatusDTO {
    return CurrentStatusDTO(this.status, this.timestamp)
}
