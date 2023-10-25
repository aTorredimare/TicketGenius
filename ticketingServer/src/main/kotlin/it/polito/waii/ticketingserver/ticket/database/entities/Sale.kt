package it.polito.waii.ticketingserver.ticket.database.entities

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.sql.Timestamp

@Entity
data class Sale(
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sale_seq")
    @SequenceGenerator(name = "sale_seq", sequenceName = "sale_seq", initialValue = 5)
    var saleId: Long,
    var timestamp: Timestamp,
    var warranty: Timestamp,
    var price: Double,
    var customerId: String,
    var ean: String,
    @OneToMany(mappedBy = "sale", cascade = [CascadeType.ALL])
    var tickets: MutableList<Ticket>
)
