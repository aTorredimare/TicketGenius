package it.polito.waii.ticketingserver.ticket.services

import it.polito.waii.ticketingserver.exceptions.HistoryNotFoundException
import it.polito.waii.ticketingserver.exceptions.TicketNotFoundException
import it.polito.waii.ticketingserver.observation.annotation.LogInfo
import it.polito.waii.ticketingserver.ticket.database.entities.TicketStatusHistory
import it.polito.waii.ticketingserver.ticket.database.repositories.TicketRepository
import it.polito.waii.ticketingserver.ticket.database.repositories.TicketStatusHistoryRepository
import it.polito.waii.ticketingserver.ticket.dtos.CurrentStatusDTO
import it.polito.waii.ticketingserver.ticket.dtos.TicketStatusHistoryDTO
import it.polito.waii.ticketingserver.ticket.dtos.toDTO
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
@LogInfo
class TicketStatusHistoryServiceImpl(
    private val ticketStatusHistoryRepository: TicketStatusHistoryRepository,
    private val ticketRepository: TicketRepository
): TicketStatusHistoryService {
    override fun findAllByTicketId(ticketId: Long): List<TicketStatusHistoryDTO>? {
        val retrievedHistory = ticketStatusHistoryRepository.findByTicketTicketId(ticketId)?.map{ it.toDTO() }
            ?: throw HistoryNotFoundException("No history for ticket with id $ticketId")

        if(retrievedHistory.isNotEmpty())
            return retrievedHistory.sortedBy { it.timestamp } //returns history from newer to older status
        else
            throw HistoryNotFoundException("No history for ticket with id $ticketId")
    }

    override fun findCurrentTicketStatus(ticketId: Long): CurrentStatusDTO? {
        val retrievedStatus = ticketStatusHistoryRepository.findByTicketTicketId(ticketId)?.map{ it.toDTO() }
            ?: throw HistoryNotFoundException("No history for ticket with id $ticketId")

        if(retrievedStatus.isNotEmpty())
                return retrievedStatus.maxBy { it.timestamp }.toDTO()
            else
                throw HistoryNotFoundException("No history for ticket with id $ticketId")

    }

    override fun findAllCurrentTicketStatuses(): List<TicketStatusHistoryDTO>? {
        val tickets = ticketRepository.findAll().map{ it.toDTO() }
        val allStatuses = ticketStatusHistoryRepository.findAll().map{ it.toDTO() }
        val statusesToReturn = mutableListOf<TicketStatusHistoryDTO>()

        if(tickets.isNotEmpty()) {
            tickets.forEach {
                val item = allStatuses.filter { s -> s.ticketID == it.ticketId }.maxBy { s -> s.timestamp }
                statusesToReturn.add(item)
            }
        } else{
            throw TicketNotFoundException("Problem while retrieving tickets")
        }

        return statusesToReturn
    }

    override fun setNewTicketStatus(ticketId: Long, newStatus: ETicketStatus): TicketStatusHistoryDTO? {
        val ticket = ticketRepository.findByIdOrNull(ticketId)

        if(ticket != null){
            val statusToAdd = TicketStatusHistory(null, newStatus, Timestamp.from(Instant.now()), ticket)
            ticketStatusHistoryRepository.save( statusToAdd )
            return statusToAdd.toDTO()
        }
        else
            throw TicketNotFoundException("Cannot change status, ticket with id $ticketId not found")
    }
}