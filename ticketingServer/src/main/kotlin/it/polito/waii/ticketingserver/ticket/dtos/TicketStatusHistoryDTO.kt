package it.polito.waii.ticketingserver.ticket.dtos

import it.polito.waii.ticketingserver.ticket.database.entities.TicketStatusHistory
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import java.sql.Timestamp

data class TicketStatusHistoryDTO (
    @field:NotNull(message="Invalid id: id has not been provided")
    @field:NotBlank(message = "Id is required.")
    val historyId: Long?,

    @field:NotNull(message="Status missing")
    val status: ETicketStatus,

    @field:Past
    val timestamp: Timestamp,

    @field:NotNull(message="Ticket is required")
    val ticketID: Long
)

fun TicketStatusHistory.toDTO(): TicketStatusHistoryDTO {
    return TicketStatusHistoryDTO(historyId, status, timestamp, ticket.ticketId)
}
