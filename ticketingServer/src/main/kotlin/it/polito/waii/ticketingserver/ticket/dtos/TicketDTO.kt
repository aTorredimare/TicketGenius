package it.polito.waii.ticketingserver.ticket.dtos

import it.polito.waii.ticketingserver.ticket.database.entities.Employee
import it.polito.waii.ticketingserver.ticket.database.entities.Sale
import it.polito.waii.ticketingserver.ticket.database.entities.Ticket
import it.polito.waii.ticketingserver.ticket.models.EPriority
import jakarta.validation.constraints.*

data class TicketDTO(
    @field:NotNull
    var ticketId: Long,

    val priority: EPriority?,
    @field:NotBlank
    val message: String,

    val expertId: String?,

    @field:NotNull
    val saleId: Long,
)

fun Ticket.toDTO(): TicketDTO {
    return TicketDTO(this.ticketId, this.priority, this.message, this.expert?.employeeId, this.sale.saleId)
}

fun fromDTO(ticketDTO: TicketDTO, employee: Employee?, sale: Sale): Ticket {
    return Ticket(
        ticketDTO.ticketId,
        ticketDTO.priority,
        ticketDTO.message,
        employee,
        sale,
    )
}
