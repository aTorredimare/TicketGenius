package it.polito.waii.ticketingserver.ticket.dtos

import it.polito.waii.ticketingserver.ticket.database.entities.Employee
import it.polito.waii.ticketingserver.ticket.models.EExpertiseArea
import it.polito.waii.ticketingserver.ticket.models.ERole
import jakarta.validation.constraints.*
import java.time.LocalDate

data class EmployeeDTO(
    @field:NotNull(message="Invalid id: id has not been provided")
    @field:NotBlank(message = "Id is required.")
    val employeeId: String,

    @field:Past
    val birthdate: LocalDate,

    @field:Email(message="Invalid mail")
    @field:NotNull(message="Invalid mail: mail has not been provided")
    @field:NotBlank(message = "Email is required.")
    val email: String,

    @field:NotBlank(message = "Expertise area is required")
    val expertiseArea: EExpertiseArea?,

    @field:NotBlank(message = "Name is required")
    val name: String,

    @field:NotBlank(message = "phone number is mandatory")
    @field:Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    val phonenumber: String,

    @field:NotBlank(message = "Role is required")
    val role: ERole,

    @field:NotBlank(message = "Surname is required")
    val surname: String,
)

fun Employee.toDTO(): EmployeeDTO {
    return EmployeeDTO(employeeId, birthdate, email, expertiseArea, name, phonenumber, role, surname)
}

fun fromDTO(employeeDTO: EmployeeDTO): Employee {
    return Employee(
        employeeDTO.employeeId,
        employeeDTO.name,
        employeeDTO.surname,
        employeeDTO.email,
        employeeDTO.birthdate,
        employeeDTO.phonenumber,
        employeeDTO.role,
        employeeDTO.expertiseArea,
    )
}