package it.polito.waii.ticketingserver.authentication.signup

import it.polito.waii.ticketingserver.authentication.dtos.KeyCloakUserDTO
import it.polito.waii.ticketingserver.authentication.dtos.SignUpExpertDTO
import it.polito.waii.ticketingserver.authentication.dtos.SignUpUserDTO
import it.polito.waii.ticketingserver.exceptions.*
import it.polito.waii.ticketingserver.observation.annotation.LogInfo
import it.polito.waii.ticketingserver.profile.dtos.ProfileDTO
import it.polito.waii.ticketingserver.profile.services.ProfileService
import it.polito.waii.ticketingserver.ticket.dtos.EmployeeDTO
import it.polito.waii.ticketingserver.ticket.models.ERole
import it.polito.waii.ticketingserver.ticket.services.EmployeeService
import org.apache.http.HttpStatus
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
@LogInfo
class SignUpServiceImpl(
    private val keyclaok: Keycloak,
    @Value("\${keycloak.realm}")
    private val realm: String,
    private val profileService: ProfileService,
    private val employeeService: EmployeeService
) : SignupService{

    override fun addUser(signUpUserDTO: SignUpUserDTO): ProfileDTO? {
        val credentials = CredentialRepresentation()
        val keycloakUserDTO = KeyCloakUserDTO.fromSignUpUserDTO(signUpUserDTO)

        credentials.isTemporary = false
        credentials.type = CredentialRepresentation.PASSWORD
        credentials.value = keycloakUserDTO.password

        val user: UserRepresentation = KeyCloakUserDTO.toUserRepresentation(keycloakUserDTO, credentials)

        val userResponse = keyclaok.realm(realm).users().create(user)
        if (userResponse.status == HttpStatus.SC_CONFLICT) throw DuplicateProfileException("The user with mail ${keycloakUserDTO.emailId} already exists.")
        if (userResponse.status == HttpStatus.SC_OK) throw KeyCloakGenericException("Something went wrong with the creation of user: ${keycloakUserDTO.emailId}. Please try again.")

        // get the role from the realm
        val role = keyclaok.realm(realm).roles().get("app_client").toRepresentation()
        // get the user id from the previous response
        val userID = CreatedResponseUtil.getCreatedId(userResponse)

        try {
            val userResource: UserResource = keyclaok.realm(realm).users().get(userID)
            userResource.roles().realmLevel().add(listOf(role))
        } catch (e: Exception) {
            // rollback
            keyclaok.realm(realm).users().get(userID).remove()
            throw RoleNotFoundException("Error during the assignment of the user role for user ${keycloakUserDTO.emailId}.")
        }

        try {
            val profileDTO = ProfileDTO(
                userID,
                signUpUserDTO.email,
                signUpUserDTO.name,
                signUpUserDTO.surname,
                signUpUserDTO.birthdate,
                signUpUserDTO.phonenumber
            )
            return profileService.createNewProfile(profileDTO)
        } catch (e: Exception) {
            // rollback
            keyclaok.realm(realm).users().get(userID).remove()
            throw DuplicateProfileException("Profile ${signUpUserDTO.email} already exists.")
        }
    }

    override fun addExpert(signUpExpertDTO: SignUpExpertDTO): EmployeeDTO? {
        val credentials = CredentialRepresentation()
        val keycloakExpertDTO = KeyCloakUserDTO.fromSignUpExpertDTO(signUpExpertDTO)

        credentials.isTemporary = false
        credentials.type = CredentialRepresentation.PASSWORD
        credentials.value = keycloakExpertDTO.password

        if (signUpExpertDTO.role.toString() == "")
            throw EmptyExpertiseAreaException("Expertise Area of the new expert must be indicated!")
        if (signUpExpertDTO.role.toString() == "" || signUpExpertDTO.role != ERole.EXPERT)
            throw EmptyRoleException("Role must be set to EXPERT!")

        val expert: UserRepresentation = KeyCloakUserDTO.toUserRepresentation(keycloakExpertDTO, credentials)

        val expertResponse = keyclaok.realm(realm).users().create(expert)
        if (expertResponse.status == HttpStatus.SC_CONFLICT) throw DuplicateProfileException("The expert with mail ${keycloakExpertDTO.emailId} already exists.")
        if (expertResponse.status == HttpStatus.SC_OK) throw KeyCloakGenericException("Something went wrong with the creation of expert: ${keycloakExpertDTO.emailId}. Please try again.")

        // get the role from the realm
        val role = keyclaok.realm(realm).roles().get("app_expert").toRepresentation()
        // get the user id from the previous response
        val expertID = CreatedResponseUtil.getCreatedId(expertResponse)

        try {
            val userResource: UserResource = keyclaok.realm(realm).users().get(expertID)
            userResource.roles().realmLevel().add(listOf(role))
        } catch (e: Exception) {
            // rollback
            keyclaok.realm(realm).users().get(expertID).remove()
            throw RoleNotFoundException("Error during the assignment of the user role for user ${signUpExpertDTO.email}.")
        }

        try {
            val expertDTO = EmployeeDTO(
                expertID,
                signUpExpertDTO.birthdate,
                signUpExpertDTO.email,
                signUpExpertDTO.expertiseArea,
                signUpExpertDTO.name,
                signUpExpertDTO.phonenumber,
                signUpExpertDTO.role,
                signUpExpertDTO.surname
            )
            return employeeService.createNewEmployee(expertDTO)
        } catch (e: Exception) {
            // rollback
            keyclaok.realm(realm).users().get(expertID).remove()
            throw DuplicateProfileException("Expert ${signUpExpertDTO.email} already exists.")
        }

    }
}