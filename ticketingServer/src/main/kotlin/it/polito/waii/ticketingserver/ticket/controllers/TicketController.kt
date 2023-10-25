package it.polito.waii.ticketingserver.ticket.controllers


import io.micrometer.observation.annotation.Observed
import it.polito.waii.ticketingserver.exceptions.DuplicateTicketException
import it.polito.waii.ticketingserver.exceptions.TicketNotFoundException
import it.polito.waii.ticketingserver.exceptions.TicketWithNoSaleException
import it.polito.waii.ticketingserver.ticket.dtos.StatusDTO
import it.polito.waii.ticketingserver.ticket.dtos.TicketDTO
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import it.polito.waii.ticketingserver.ticket.services.TicketService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp

@RestController
@Observed
class TicketController(private val ticketService: TicketService) {
    @GetMapping("/API/tickets/id/{ticketId}")
    fun getTicketById(@PathVariable ticketId: Long): TicketDTO? {
        return ticketService.findTicketById(ticketId)
    }
    @GetMapping("/API/tickets/expert/{expertId}")
    fun getTicketsByExpert(@PathVariable expertId: String):  List<TicketDTO>? {
        return ticketService.findAllByExpert(expertId)
    }
    @GetMapping("/API/tickets/customer/{customerId}")
    fun getTicketsByCustomer(@PathVariable customerId: String): List<TicketDTO>? {
        return ticketService.findAllByCustomerId(customerId)
    }
    @GetMapping("/API/tickets/checkbyid/{ticketId}")
    fun checkIfTicketExistsById(@PathVariable ticketId: Long): Boolean {
        return ticketService.existsByTicketId(ticketId)
    }
    @GetMapping("/API/tickets/checkbyexpert/{expertId}")
    fun checkIfTicketExistsByExpert(@PathVariable expertId: String): Boolean {
        return ticketService.existsByExpert(expertId)
    }
    @GetMapping("/API/tickets/status/{status}")
    fun getTicketsByStatus(@PathVariable status: ETicketStatus): List<TicketDTO>?{
        return ticketService.findAllByStatus(status)
    }
    @GetMapping("/API/tickets/{ticketId}/opened")
    fun getOpeningTimestamp(@PathVariable ticketId: Long): Timestamp?{
        return ticketService.getOpeningTimestamp(ticketId)
    }
    @PostMapping("/API/ticket")
    fun createNewTicket(@Valid @RequestBody ticketDTO: TicketDTO): TicketDTO? {
        try{
            return ticketService.createNewTicket(ticketDTO)
        } catch(ex: DuplicateTicketException) {
            throw ex
        }
    }

    @PutMapping("/API/ticket/assign/{ticketId}")
    //the ticketDTO in the body only needs priority and expertId set, ticketId is a query param
    //we need to pass an object for the request body, we use a TicketDTO with priority and expertID set to the values
    //we want to add
    fun updateTicketExpert(@PathVariable ticketId: Long, @Valid @RequestBody ticket: TicketDTO): TicketDTO? {
        try{
            return ticketService.updateTicketExpert(ticket.expertId, ticket.priority, ticketId)
        } catch(ex: TicketNotFoundException) {
            throw ex
        } catch (ex: TicketWithNoSaleException) {
            throw ex
        }
    }

    @PutMapping("/API/ticket/changestatus/{ticketId}")
    fun changeTicketStatus(@PathVariable ticketId: Long, @RequestBody newStatusDTO: StatusDTO): TicketDTO? {
        try{
            return ticketService.changeTicketStatus(ticketId, newStatusDTO.newStatus)
        } catch(ex: TicketNotFoundException) {
            throw ex
        }
    }
}

