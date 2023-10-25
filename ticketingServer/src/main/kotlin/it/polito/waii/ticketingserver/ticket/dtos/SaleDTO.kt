package it.polito.waii.ticketingserver.ticket.dtos

import it.polito.waii.ticketingserver.ticket.database.entities.Sale
import it.polito.waii.ticketingserver.ticket.database.entities.Ticket
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Past
import java.sql.Timestamp

data class SaleDTO (
    @field:NotNull(message="Invalid id: id has not been provided")
    val saleId: Long,

    @field:Past
    val timestamp: Timestamp,

    @field:Future
    val warranty: Timestamp,

    @field:NotNull(message="Price is required")
    val price: Double,

    @field:NotNull(message="customerId is required")
    val customerId: String,

    @field:NotNull(message="Product ean is required")
    val ean: String,

    val ticketIds: Set<Long>,
)

fun Sale.toDTO(): SaleDTO {
    val ticketIDs = tickets.map { it.ticketId }.toSet()
    return SaleDTO(saleId, timestamp, warranty, price, customerId, ean, ticketIDs)
}

fun fromDTO(saleDTO: SaleDTO): Sale{
    val tickets = mutableListOf<Ticket>() //for the moment it's returned empty
    return Sale(
        saleDTO.saleId,
        saleDTO.timestamp,
        saleDTO.warranty,
        saleDTO.price,
        saleDTO.customerId,
        saleDTO.ean,
        tickets,
    )
}
