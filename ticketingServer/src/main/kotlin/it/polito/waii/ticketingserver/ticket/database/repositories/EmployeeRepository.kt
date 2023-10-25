package it.polito.waii.ticketingserver.ticket.database.repositories

import it.polito.waii.ticketingserver.ticket.database.entities.Employee
import it.polito.waii.ticketingserver.ticket.models.EExpertiseArea
import it.polito.waii.ticketingserver.ticket.models.ERole
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface EmployeeRepository: JpaRepository<Employee, String> {
    fun findEmployeeByEmail(email: String): Employee?
    fun findEmployeesByRole(role: ERole): List<Employee>?
    fun findEmployeesByExpertiseArea(expertiseArea: EExpertiseArea): List<Employee>?
}