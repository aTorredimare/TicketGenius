package it.polito.waii.ticketingserver.ticket.database.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
data class TicketStatusHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_status_history_seq")
    @SequenceGenerator(name = "ticket_status_history_seq", sequenceName = "ticket_status_history_seq", initialValue = 3)
    var historyId: Long?,
    @Enumerated(value = EnumType.STRING)
    var status: ETicketStatus,
    var timestamp: Timestamp,
    @ManyToOne
    @JsonIgnoreProperties("statusHistory")
    var ticket: Ticket
)


