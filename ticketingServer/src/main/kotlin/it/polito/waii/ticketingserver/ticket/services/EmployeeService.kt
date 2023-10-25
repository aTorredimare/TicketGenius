package it.polito.waii.ticketingserver.ticket.services

import it.polito.waii.ticketingserver.ticket.dtos.EmployeeDTO
import it.polito.waii.ticketingserver.ticket.models.EExpertiseArea
import it.polito.waii.ticketingserver.ticket.models.ERole
import jakarta.validation.Valid

interface EmployeeService {
    fun findEmployeeByEmployeeId(employeeId: String) : EmployeeDTO?
    fun findEmployeeByEmail(email: String) : EmployeeDTO?
    fun findAllByRole(role: ERole) : List<EmployeeDTO>?
    fun findAllByExpertiseArea(expertiseArea: EExpertiseArea) : List<EmployeeDTO>?
    fun existsByEmployeeId(employeeId: String) : Boolean
    fun existsByEmail(email: String) : Boolean
    fun createNewEmployee(@Valid employeeDTO: EmployeeDTO): EmployeeDTO?

}