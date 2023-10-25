package it.polito.waii.ticketingserver.ticket.services

import it.polito.waii.ticketingserver.exceptions.DuplicateEmployeeException
import it.polito.waii.ticketingserver.exceptions.EmployeeNotFoundException
import it.polito.waii.ticketingserver.observation.annotation.LogInfo
import it.polito.waii.ticketingserver.ticket.database.repositories.EmployeeRepository
import it.polito.waii.ticketingserver.ticket.dtos.EmployeeDTO
import it.polito.waii.ticketingserver.ticket.dtos.fromDTO
import it.polito.waii.ticketingserver.ticket.dtos.toDTO
import it.polito.waii.ticketingserver.ticket.models.EExpertiseArea
import it.polito.waii.ticketingserver.ticket.models.ERole
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
@LogInfo
class EmployeeServiceImpl(private val employeeRepository: EmployeeRepository): EmployeeService {
    override fun findEmployeeByEmployeeId(employeeId: String): EmployeeDTO? {
        val retrievedEmployee = employeeRepository.findByIdOrNull(employeeId)?.toDTO()
        if(retrievedEmployee != null)
            return retrievedEmployee
        else
            throw EmployeeNotFoundException("No employee with id $employeeId found.")
    }

    override fun findEmployeeByEmail(email: String): EmployeeDTO? {
//        val retrievedEmployee = employeeRepository
//            .findAll()
//            .map { it.toDTO() }
//            .filter { it.email == email}

//        if(retrievedEmployee.isNotEmpty())
//            return retrievedEmployee[0]
//        else
//            throw EmployeeNotFoundException("No employee with mail $email found.")

        val retrievedEmployee = employeeRepository
            .findEmployeeByEmail(email)
            ?.toDTO()

        if(retrievedEmployee != null)
            return retrievedEmployee
        else
            throw EmployeeNotFoundException("No employee with mail $email found.")
    }

    override fun findAllByRole(role: ERole): List<EmployeeDTO>? {
/*        val retrievedEmployees = employeeRepository
            .findAll()
            .map { it.toDTO() }
            .filter { it.role == role}
        if(retrievedEmployees.isNotEmpty())
            return retrievedEmployees
        else
            throw EmployeeNotFoundException("No employee with role $role found.")*/

        val retrievedEmployees = employeeRepository
            .findEmployeesByRole(role)
            ?.map { it.toDTO() }

        if(!retrievedEmployees.isNullOrEmpty())
            return retrievedEmployees
        else
            throw EmployeeNotFoundException("No employee with role $role found.")


    }

    override fun findAllByExpertiseArea(expertiseArea: EExpertiseArea): List<EmployeeDTO>? {
        /*val retrievedEmployees = employeeRepository
            .findAll()
            .map { it.toDTO() }
            .filter { it.expertiseArea == expertiseArea}

        if(retrievedEmployees.isNotEmpty())
            return retrievedEmployees
        else
            throw EmployeeNotFoundException("No employee with expertise $expertiseArea found.")*/

        val retrievedEmployees = employeeRepository
            .findEmployeesByExpertiseArea(expertiseArea)
            ?.map { it.toDTO() }

        if(!retrievedEmployees.isNullOrEmpty())
            return retrievedEmployees
        else
            throw EmployeeNotFoundException("No employee with expertise $expertiseArea found.")
    }

    override fun existsByEmployeeId(employeeId: String): Boolean {
        val employeeExists = employeeRepository.findByIdOrNull(employeeId)?.toDTO()
        return employeeExists != null
    }

    override fun existsByEmail(email: String): Boolean {
        /*val employeeExists = employeeRepository
            .findAll()
            .map { it.toDTO() }
            .filter { it.email == email}

        return employeeExists.isNotEmpty()*/

        val employeeExists = employeeRepository
            .findEmployeeByEmail(email)
            ?.toDTO()

        return employeeExists != null
    }

    override fun createNewEmployee(employeeDTO: EmployeeDTO): EmployeeDTO? {
        val employee = fromDTO(employeeDTO)
        val exists = employeeRepository.findByIdOrNull(employee.employeeId)?.toDTO()

        if(exists == null) {
            employeeRepository.save(employee)
            return employee.toDTO()
        }
        else
            throw DuplicateEmployeeException("Employee already exists")
    }
}