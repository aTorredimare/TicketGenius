package it.polito.waii.ticketingserver.ticket.database.entities

import com.fasterxml.jackson.annotation.JsonIgnore
import it.polito.waii.ticketingserver.chat.database.entities.Chat
import it.polito.waii.ticketingserver.ticket.models.EPriority
import jakarta.persistence.*

@Entity
data class Ticket(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ticket_seq")
    @SequenceGenerator(name = "ticket_seq", sequenceName = "ticket_seq", initialValue = 3)
    var ticketId: Long,
    @Enumerated(value = EnumType.STRING)
    var priority: EPriority?,
    var message: String,
    @OneToOne
    @JoinColumn(name = "expertId", nullable = true)
    var expert: Employee?,
    @ManyToOne
    @JoinColumn(name = "saleId", nullable = false)
    @JsonIgnore
    var sale: Sale,
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "ticket")
    val chat: MutableList<Chat> = mutableListOf(),
    @OneToMany(cascade = [CascadeType.ALL], mappedBy = "ticket")
    val statusHistory: MutableList<TicketStatusHistory> = mutableListOf()
)
