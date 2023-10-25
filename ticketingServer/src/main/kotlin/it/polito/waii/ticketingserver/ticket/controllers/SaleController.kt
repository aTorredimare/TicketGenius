package it.polito.waii.ticketingserver.ticket.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.waii.ticketingserver.exceptions.DuplicateSaleException
import it.polito.waii.ticketingserver.ticket.dtos.SaleDTO
import it.polito.waii.ticketingserver.ticket.services.SaleService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Observed
class SaleController(private val saleService: SaleService) {
    @GetMapping("/API/sales/sale/{saleId}")
    fun getSaleById(@PathVariable saleId: Long): SaleDTO? {
        return saleService.findSaleBySaleId(saleId)
    }

    @GetMapping("/API/sales/customer/{customerId}")
    fun getSalesByCustomerId(@PathVariable customerId: String): List<SaleDTO>? {
        return saleService.findSaleByCustomerId(customerId)
    }

    @PostMapping("/API/sales")
    fun createNewSale(@Valid @RequestBody saleDTO: SaleDTO): SaleDTO? {
        try{
            return saleService.createNewSale(saleDTO)
        } catch(ex: DuplicateSaleException) {
            throw ex
        }
    }
}
