package it.polito.waii.ticketingserver.ticket.database.repositories

import it.polito.waii.ticketingserver.ticket.database.entities.Ticket
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TicketRepository: JpaRepository<Ticket, Long> {

}