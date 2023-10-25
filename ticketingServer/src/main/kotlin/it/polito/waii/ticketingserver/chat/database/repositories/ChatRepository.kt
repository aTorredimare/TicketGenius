package it.polito.waii.ticketingserver.chat.database.repositories

import it.polito.waii.ticketingserver.chat.database.entities.Chat
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface ChatRepository : JpaRepository<Chat, Long> {
    //fun findAllByTicketTicketId(ticketId: Long) : List<Chat>?
    @Query(
        value = """
            SELECT * FROM chat
            WHERE ticket_ticket_id = :ticketId
            ORDER BY "timestamp" DESC
            LIMIT 10
        """,
        nativeQuery=true
    )
    fun findLatest(@Param("ticketId") ticketId: Long): List<Chat>?

    @Query(
        value = """
        SELECT * FROM (
            SELECT * FROM chat
            WHERE timestamp > (SELECT timestamp FROM chat WHERE chat_id = :messageId AND ticket_ticket_id = :ticketId)
            ORDER BY "timestamp" DESC
        ) as "chat2" ORDER BY "timestamp"
        """,
        nativeQuery = true
    )
    fun findLatest(@Param("ticketId") ticketId: Long, @Param("messageId") messageId: Long): List<Chat>?

}
