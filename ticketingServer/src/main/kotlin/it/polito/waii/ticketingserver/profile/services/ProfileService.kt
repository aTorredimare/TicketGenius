package it.polito.waii.ticketingserver.profile.services

import it.polito.waii.ticketingserver.authentication.dtos.KeyCloakUserDTO
import it.polito.waii.ticketingserver.authentication.dtos.SignUpUserDTO
import it.polito.waii.ticketingserver.profile.dtos.ProfileDTO
import jakarta.validation.Valid

interface ProfileService {
    fun getAllProfiles(): List<ProfileDTO>
    fun getProfile(email: String): ProfileDTO?
    fun getProfileById(profileId: String): ProfileDTO?
    fun createNewProfile(@Valid profileDTO: ProfileDTO): ProfileDTO?
    fun updateProfile(email: String, profileDTO: ProfileDTO) : ProfileDTO?

}
