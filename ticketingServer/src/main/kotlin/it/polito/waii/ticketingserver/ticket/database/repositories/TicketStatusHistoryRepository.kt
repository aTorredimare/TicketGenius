package it.polito.waii.ticketingserver.ticket.database.repositories

import it.polito.waii.ticketingserver.ticket.database.entities.TicketStatusHistory
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface TicketStatusHistoryRepository : JpaRepository<TicketStatusHistory, Long> {
    fun findByTicketTicketId(ticketId: Long): List<TicketStatusHistory>?

    @Query(
        value = """
            SELECT * FROM ticket_status_history
            WHERE ticket_ticket_id = :ticketId AND status = :status
            
        """,
        nativeQuery = true
    )
    fun findOpeningTimestamp(
        @Param("ticketId") ticketId: Long,
        @Param("status") status: String
    ): TicketStatusHistory?
}
