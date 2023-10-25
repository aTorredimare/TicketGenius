package it.polito.waii.ticketingserver.ticket.database.entities

import it.polito.waii.ticketingserver.ticket.models.EExpertiseArea
import it.polito.waii.ticketingserver.ticket.models.ERole
import jakarta.persistence.*
import java.time.LocalDate

@Entity
data class Employee(
    @Id
    var employeeId: String,
    var name: String,
    var surname: String,
    var email: String,
    var birthdate: LocalDate,
    var phonenumber: String,
    @Enumerated(value = EnumType.STRING)
    var role: ERole,
    @Enumerated(value = EnumType.STRING)
    var expertiseArea: EExpertiseArea?
)
