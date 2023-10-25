package it.polito.waii.ticketingserver.ticket.database.repositories

import it.polito.waii.ticketingserver.ticket.database.entities.Sale
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface SaleRepository : JpaRepository<Sale, Long> {
    fun findAllByCustomerId(customerId: String): List<Sale>?
}