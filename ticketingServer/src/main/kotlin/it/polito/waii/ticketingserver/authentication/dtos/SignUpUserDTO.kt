package it.polito.waii.ticketingserver.authentication.dtos

import jakarta.validation.constraints.*
import java.time.LocalDate

data class SignUpUserDTO (
    @field:Email(message="Invalid mail")
    @field:NotNull(message="Invalid mail: mail has not been provided")
    @field:NotBlank(message = "Email is required.")
    val email: String,
    @field:NotBlank(message = "Name is required")
    val name: String,
    @field:NotBlank(message = "Surname is required")
    val surname: String,
    @field:Past
    val birthdate: LocalDate,
    @field:NotBlank(message = "phone number is mandatory")
    @field:Pattern(regexp = "^\\d{10}$", message = "Invalid phone number")
    val phonenumber: String,
    val password: String,
    )