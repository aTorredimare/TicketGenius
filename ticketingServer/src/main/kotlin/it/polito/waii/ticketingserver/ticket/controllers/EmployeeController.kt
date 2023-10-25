package it.polito.waii.ticketingserver.ticket.controllers

import io.micrometer.observation.annotation.Observed
import it.polito.waii.ticketingserver.exceptions.DuplicateEmployeeException
import it.polito.waii.ticketingserver.ticket.dtos.EmployeeDTO
import it.polito.waii.ticketingserver.ticket.models.EExpertiseArea
import it.polito.waii.ticketingserver.ticket.models.ERole
import it.polito.waii.ticketingserver.ticket.services.EmployeeService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Observed
class EmployeeController(private val employeeService: EmployeeService) {
    @GetMapping("/API/employees/id/{employeeId}")
    fun getEmployeeById(@PathVariable employeeId: String): EmployeeDTO? {
        return employeeService.findEmployeeByEmployeeId(employeeId)
    }

    /*
    @GetMapping("/API/test")
    fun test(authentication: Authentication): String {
        return authentication.toString()
    }

    @GetMapping("/API/employees/mail/{mail}")
    fun getEmployeeByMail(@PathVariable mail: String): EmployeeDTO? {
        return employeeService.findEmployeeByEmail(mail)
    }*/

    @GetMapping("/API/employees/role/{role}")
    fun getEmployeesByRole(@PathVariable role: ERole): List<EmployeeDTO>? {
        return employeeService.findAllByRole(role)
    }

    @GetMapping("/API/employees/exparea/{expertiseArea}")
    fun getEmployeesByExpertiseArea(@PathVariable expertiseArea: EExpertiseArea): List<EmployeeDTO>? {
        return employeeService.findAllByExpertiseArea(expertiseArea)
    }

    @GetMapping("/API/employees/checkbyid/{employeeId}")
    fun checkIfEmployeeExistsById(@PathVariable employeeId: String): Boolean {
        return employeeService.existsByEmployeeId(employeeId)
    }

    @GetMapping("/API/employees/checkbymail/{mail}")
    fun checkIfEmployeeExistsByMail(@PathVariable mail: String): Boolean {
        return employeeService.existsByEmail(mail)
    }
}