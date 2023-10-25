package it.polito.waii.ticketingserver.ticket.services

import it.polito.waii.ticketingserver.exceptions.DuplicateSaleException
import it.polito.waii.ticketingserver.exceptions.SaleNotFoundException
import it.polito.waii.ticketingserver.observation.annotation.LogInfo
import it.polito.waii.ticketingserver.ticket.database.repositories.SaleRepository
import it.polito.waii.ticketingserver.ticket.dtos.SaleDTO
import it.polito.waii.ticketingserver.ticket.dtos.fromDTO
import it.polito.waii.ticketingserver.ticket.dtos.toDTO
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
@LogInfo
class SalesServiceImpl(private val saleRepository: SaleRepository): SaleService {
    override fun findSaleBySaleId(saleId: Long): SaleDTO? {
        val retrievedSale = saleRepository.findByIdOrNull(saleId)?.toDTO()
        if(retrievedSale != null)
            return retrievedSale
        else
            throw SaleNotFoundException("No sale with id $saleId found")
    }

    override fun findSaleByCustomerId(customerId: String): List<SaleDTO>? {
        val retrievedSales = saleRepository.findAllByCustomerId(customerId)?.map { it.toDTO() }
        if(retrievedSales != null)
            return retrievedSales
        else
            throw SaleNotFoundException("No sales with customerId $customerId found")
    }

    override fun createNewSale(saleDTO: SaleDTO): SaleDTO? {
        val s = fromDTO(saleDTO)
        val exists = saleRepository.findByIdOrNull(s.saleId)?.toDTO()

        if(exists == null){
            //here we may need to retrieve the list of tickets for this sale
            //fromDTO method returns an empty list of tickets (see SaleDTO)
            val createdSale = saleRepository.save(s)
            return createdSale.toDTO()
        }
        else
            throw DuplicateSaleException("Sale already exists")
    }
}
