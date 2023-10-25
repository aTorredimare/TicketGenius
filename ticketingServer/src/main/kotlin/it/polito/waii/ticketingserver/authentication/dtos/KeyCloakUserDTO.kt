package it.polito.waii.ticketingserver.authentication.dtos

import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation

class KeyCloakUserDTO(
    val userName: String,
    val emailId: String,
    val password: String,
    val firstname: String,
    val lastName: String) {


    companion object {
        fun toUserRepresentation(userDTO: KeyCloakUserDTO, credentials: CredentialRepresentation): UserRepresentation{
            val user = UserRepresentation()
            user.username = userDTO.userName
            user.firstName = userDTO.firstname
            user.lastName = userDTO.lastName
            user.email = userDTO.emailId
            user.credentials = listOf(credentials)
            user.isEnabled = true
            user.isEmailVerified = true

            return user
        }

        fun fromSignUpUserDTO(signUpUserDTO: SignUpUserDTO): KeyCloakUserDTO{
            return KeyCloakUserDTO(
                signUpUserDTO.email,
                signUpUserDTO.email,
                signUpUserDTO.password,
                signUpUserDTO.name,
                signUpUserDTO.surname
            )
        }

        fun fromSignUpExpertDTO(signUpExpertDTO: SignUpExpertDTO): KeyCloakUserDTO{
            return KeyCloakUserDTO(
                signUpExpertDTO.email,
                signUpExpertDTO.email,
                signUpExpertDTO.password,
                signUpExpertDTO.name,
                signUpExpertDTO.surname
            )
        }
    }
}
