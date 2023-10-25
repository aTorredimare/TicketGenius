package it.polito.waii.ticketingserver.ticket.services

import it.polito.waii.ticketingserver.ticket.dtos.TicketDTO
import it.polito.waii.ticketingserver.ticket.models.EPriority
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import jakarta.validation.Valid
import java.sql.Timestamp

interface TicketService {
    fun findTicketById(ticketId: Long) : TicketDTO?
    fun findAllByExpert(expertId: String) : List<TicketDTO>?
    fun findAllByCustomerId(customerId: String) : List<TicketDTO>?
    fun findAllByStatus(status: ETicketStatus) : List<TicketDTO>?
    fun existsByTicketId(ticketId: Long) : Boolean
    fun existsByExpert(expertId: String) : Boolean
    fun createNewTicket(@Valid ticketDTO: TicketDTO): TicketDTO
    fun updateTicketExpert(expertId: String?, ePriority: EPriority?, ticketId: Long): TicketDTO?
    fun changeTicketStatus(ticketId: Long, newStatus: ETicketStatus): TicketDTO?
    fun getOpeningTimestamp(ticketId: Long): Timestamp?
}
