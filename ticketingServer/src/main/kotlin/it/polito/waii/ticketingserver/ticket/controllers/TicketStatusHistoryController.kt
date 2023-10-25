package it.polito.waii.ticketingserver.ticket.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.waii.ticketingserver.exceptions.HistoryNotFoundException
import it.polito.waii.ticketingserver.exceptions.TicketNotFoundException
import it.polito.waii.ticketingserver.ticket.dtos.CurrentStatusDTO
import it.polito.waii.ticketingserver.ticket.dtos.TicketStatusHistoryDTO
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import it.polito.waii.ticketingserver.ticket.services.TicketStatusHistoryService
import org.springframework.web.bind.annotation.*
import java.sql.Timestamp

@RestController
@Observed
class TicketStatusHistoryController(private val ticketStatusHistoryService: TicketStatusHistoryService) {
    @GetMapping("/API/tickethistory/{ticketId}")
    fun getHistoryByTickedId(@PathVariable ticketId: Long): List<TicketStatusHistoryDTO>? {
        try {
            return ticketStatusHistoryService.findAllByTicketId(ticketId)
        } catch(ex: HistoryNotFoundException){
            throw ex
        }
    }

    @GetMapping("/API/tickethistory/currentstatus/{ticketId}")
    fun getCurrentStatusByTickedId(@PathVariable ticketId: Long): CurrentStatusDTO? {
        try {
            return ticketStatusHistoryService.findCurrentTicketStatus(ticketId)
        } catch(ex: HistoryNotFoundException){
            throw ex
        }
    }

    @GetMapping("/API/tickethistory/allcurrentstatuses")
    fun getMostRecentStatuses(): List<TicketStatusHistoryDTO>? {
        try {
            return ticketStatusHistoryService.findAllCurrentTicketStatuses()
        } catch(ex: TicketNotFoundException){
            throw ex
        }
    }

    @PutMapping("/API/tickethistory/changestatus/{ticketId}")
    fun setTicketStatus(@PathVariable ticketId: Long, @RequestBody newStatus: ETicketStatus): TicketStatusHistoryDTO? {
        try {
            return ticketStatusHistoryService.setNewTicketStatus(ticketId, newStatus)
        } catch(ex: TicketNotFoundException){
            throw ex
        }

    }
}