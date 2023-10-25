package it.polito.waii.ticketingserver.authentication.signup

import it.polito.waii.ticketingserver.authentication.dtos.KeyCloakUserDTO
import it.polito.waii.ticketingserver.authentication.dtos.SignUpExpertDTO
import it.polito.waii.ticketingserver.authentication.dtos.SignUpUserDTO
import it.polito.waii.ticketingserver.profile.dtos.ProfileDTO
import it.polito.waii.ticketingserver.ticket.dtos.EmployeeDTO
import org.keycloak.representations.idm.UserRepresentation
import javax.ws.rs.core.Response

interface SignupService {
    fun addUser(signUpUserDTO: SignUpUserDTO): ProfileDTO?
    fun addExpert(signUpExpertDTO: SignUpExpertDTO): EmployeeDTO?
}