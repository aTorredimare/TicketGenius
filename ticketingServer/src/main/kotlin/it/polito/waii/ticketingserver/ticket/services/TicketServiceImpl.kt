package it.polito.waii.ticketingserver.ticket.services

import it.polito.waii.ticketingserver.exceptions.*
import it.polito.waii.ticketingserver.observation.annotation.LogInfo
import it.polito.waii.ticketingserver.ticket.database.entities.TicketStatusHistory
import it.polito.waii.ticketingserver.ticket.database.repositories.TicketRepository
import it.polito.waii.ticketingserver.ticket.database.repositories.TicketStatusHistoryRepository
import it.polito.waii.ticketingserver.ticket.database.repositories.EmployeeRepository
import it.polito.waii.ticketingserver.ticket.database.repositories.SaleRepository
import it.polito.waii.ticketingserver.ticket.dtos.TicketDTO
import it.polito.waii.ticketingserver.ticket.dtos.fromDTO
import it.polito.waii.ticketingserver.ticket.dtos.toDTO
import it.polito.waii.ticketingserver.ticket.models.EPriority
import it.polito.waii.ticketingserver.ticket.models.ETicketStatus
import jakarta.validation.Valid
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.sql.Timestamp
import java.time.Instant

@Service
@LogInfo
class TicketServiceImpl(
    private val employeeRepository: EmployeeRepository,
    private val saleRepository: SaleRepository,
    private val ticketRepository: TicketRepository,
    private val ticketStatusHistoryRepository: TicketStatusHistoryRepository
): TicketService {

    override fun findTicketById(ticketId: Long) : TicketDTO? {
        val retrievedTicket = ticketRepository.findByIdOrNull(ticketId)?.toDTO()
        if(retrievedTicket != null)
            return retrievedTicket
        else
            throw TicketNotFoundException("No ticket with id $ticketId found.")
    }
    override fun findAllByExpert(expertId: String) : List<TicketDTO>? {
        val retrievedTickets = ticketRepository
            .findAll()
            .map { it.toDTO() }
            .filter { it.expertId == expertId}

        if(retrievedTickets.isNotEmpty())
            return retrievedTickets
        else
            throw TicketNotFoundException("No ticket assigned to expert $expertId found.")
    }

    override fun findAllByCustomerId(customerId: String): List<TicketDTO>? {
        val customerSales = saleRepository.findAllByCustomerId(customerId)
        return customerSales?.flatMap { sale -> sale.tickets.map { it.toDTO() } }
    }
    override fun findAllByStatus(status: ETicketStatus): List<TicketDTO>? {
        val tickets = ticketRepository.findAll().map { it.toDTO() }
        val statuses = ticketStatusHistoryRepository.findAll().map {it.toDTO()}
        val ticketsToReturn = mutableListOf<TicketDTO>()

        if(tickets.isNotEmpty() && statuses.isNotEmpty()) {
            tickets.forEach {
                val mostRecentStatus =
                    statuses.filter { s -> s.ticketID == it.ticketId }.maxBy { s -> s.timestamp }
                if (mostRecentStatus.status == status)
                    ticketsToReturn.add(it)
            }
        }else{
            throw TicketNotFoundException("Ticket not found")
        }
        return ticketsToReturn
    }
    override fun existsByTicketId(ticketId: Long) : Boolean {
        val ticketExists = ticketRepository.findByIdOrNull(ticketId)?.toDTO()
        return ticketExists != null
    }
    override fun existsByExpert(expertId: String) : Boolean {
        val ticketExists = ticketRepository
            .findAll()
            .map { it.toDTO() }
            .filter { it.expertId == expertId}

        return ticketExists.isNotEmpty()
    }
    @Transactional
    override fun createNewTicket(@Valid ticketDTO: TicketDTO): TicketDTO {
        val sale = saleRepository.findByIdOrNull(ticketDTO.saleId)
        val now = Timestamp.from(Instant.now())

        if(sale != null) {
            if (sale.warranty < now) {
                throw ExpiredWarrantyException("Cannot open ticket if product warranty has expired")
            }
            val ticket = fromDTO(ticketDTO, null, sale)
            ticket.priority = null

            val exists = ticketRepository.findByIdOrNull(ticketDTO.ticketId)//?.toDTO()

            if(exists == null){
                val savedTicket = ticketRepository.save(ticket)
                //the value of the historyId is not important, is auto-generated
                val newStatus = TicketStatusHistory(100, ETicketStatus.OPEN, Timestamp.from(Instant.now()) , savedTicket)
                ticketStatusHistoryRepository.save( newStatus )
                return savedTicket.toDTO()
            }
            else
                throw DuplicateTicketException("Ticket already exists")
        } else
            throw TicketWithNoSaleException("Cannot create a ticket with a non valid sale.")
    }

    @Transactional
    override fun updateTicketExpert(expertId: String?, ePriority: EPriority?, ticketId: Long): TicketDTO? {
        val expert = employeeRepository.findByIdOrNull(expertId)
        val ticket = ticketRepository.findByIdOrNull(ticketId)

        if (expert != null){
            if (ticket != null) {
                if (ticket.expert != null){
                    throw TicketAlreadyHaveAnExpertException("Ticket [$ticketId] already has an expert assigned: expert ${ticket.expert!!.employeeId}")
                }
                ticket.expert = expert
                ticket.priority = ePriority
                ticketRepository.save(ticket)
            }else {
                throw TicketNotFoundException("No ticket with id $ticketId found.")
            }
            //the value of the historyId is not important, is auto-generated
            val newStatus = TicketStatusHistory(100, ETicketStatus.IN_PROGRESS, Timestamp.from(Instant.now()) , ticket)
            ticketStatusHistoryRepository.save( newStatus )
        } else
            throw EmployeeNotFoundException("No employee with id $expertId found.")
        return ticket.toDTO()
    }

    override fun changeTicketStatus(ticketId: Long, newStatus: ETicketStatus): TicketDTO? {
        val ticket = ticketRepository.findByIdOrNull(ticketId)
        val possibleFutureStatus = mapOf(
            ETicketStatus.OPEN to arrayOf(ETicketStatus.IN_PROGRESS, ETicketStatus.RESOLVED, ETicketStatus.CLOSED),
            ETicketStatus.IN_PROGRESS to arrayOf(ETicketStatus.OPEN, ETicketStatus.RESOLVED, ETicketStatus.CLOSED),
            ETicketStatus.RESOLVED to arrayOf(ETicketStatus.REOPENED, ETicketStatus.CLOSED),
            ETicketStatus.REOPENED to arrayOf(ETicketStatus.IN_PROGRESS, ETicketStatus.RESOLVED, ETicketStatus.CLOSED),
            ETicketStatus.CLOSED to arrayOf(ETicketStatus.REOPENED),
        )

        if(ticket != null) {
            if(possibleFutureStatus[ticket.statusHistory.maxBy { it.timestamp }.status]?.contains(newStatus) != true)
                throw TicketNotFoundException("Ticket new status $newStatus is in conflict with the current one")
            val newStatusHistory = TicketStatusHistory(100, newStatus, Timestamp.from(Instant.now()) , ticket)
            ticketStatusHistoryRepository.save( newStatusHistory )
        }
        else
            throw TicketNotFoundException("Ticket with id $ticketId not found.")
        return ticket.toDTO()
    }
    override fun getOpeningTimestamp(ticketId: Long): Timestamp? {
        val entry =  ticketStatusHistoryRepository
            .findOpeningTimestamp(ticketId, ETicketStatus.OPEN.toString())
        if(entry == null){
            throw HistoryNotFoundException("Problem while retrieving opening timestamp for ticket {$ticketId}")
        } else {
            return entry.timestamp
        }
    }

}
