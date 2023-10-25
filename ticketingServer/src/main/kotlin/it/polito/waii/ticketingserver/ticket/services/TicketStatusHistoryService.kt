package it.polito.waii.ticketingserver.ticket.services

import it.polito.waii.ticketingserver.ticket.dtos.CurrentStatusDTO
import it.polito.waii.ticketingserver.ticket.dtos.TicketStatusHistoryDTO
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import java.sql.Timestamp

interface TicketStatusHistoryService {
    fun findAllByTicketId(ticketId: Long) : List<TicketStatusHistoryDTO>?
    fun findCurrentTicketStatus(ticketId: Long) : CurrentStatusDTO?
    fun findAllCurrentTicketStatuses(): List<TicketStatusHistoryDTO>?
    fun setNewTicketStatus(ticketId: Long, newStatus: ETicketStatus): TicketStatusHistoryDTO?
}