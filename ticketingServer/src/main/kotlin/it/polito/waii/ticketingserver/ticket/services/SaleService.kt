package it.polito.waii.ticketingserver.ticket.services

import it.polito.waii.ticketingserver.ticket.dtos.SaleDTO
import jakarta.validation.Valid

interface SaleService {
    fun findSaleBySaleId(saleId: Long): SaleDTO?
    fun findSaleByCustomerId(customerId: String): List<SaleDTO>?
    fun createNewSale(@Valid saleDTO: SaleDTO): SaleDTO?
}
