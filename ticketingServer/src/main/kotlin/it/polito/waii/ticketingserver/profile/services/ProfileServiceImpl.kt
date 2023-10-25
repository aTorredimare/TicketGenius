package it.polito.waii.ticketingserver.profile.services

import it.polito.waii.ticketingserver.authentication.dtos.KeyCloakUserDTO
import it.polito.waii.ticketingserver.authentication.dtos.SignUpUserDTO
import it.polito.waii.ticketingserver.exceptions.DuplicateProfileException
import it.polito.waii.ticketingserver.exceptions.KeyCloakGenericException
import it.polito.waii.ticketingserver.exceptions.ProfileNotFoundException
import it.polito.waii.ticketingserver.exceptions.RoleNotFoundException
import it.polito.waii.ticketingserver.observation.annotation.LogInfo
import it.polito.waii.ticketingserver.profile.database.entities.Profile
import it.polito.waii.ticketingserver.profile.database.repositories.ProfileRepository
import it.polito.waii.ticketingserver.profile.dtos.ProfileDTO
import it.polito.waii.ticketingserver.profile.dtos.toDTO
import org.apache.http.HttpStatus
import org.keycloak.admin.client.CreatedResponseUtil
import org.keycloak.admin.client.Keycloak
import org.keycloak.admin.client.resource.UserResource
import org.keycloak.representations.idm.CredentialRepresentation
import org.keycloak.representations.idm.UserRepresentation
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import javax.ws.rs.core.Response
import kotlin.jvm.optionals.getOrNull

@Service
@LogInfo
@Transactional
class ProfileServiceImpl(
    private val profileRepository: ProfileRepository,
    private val keyclaok: Keycloak,
    @Value("\${keycloak.realm}")
    private val realm: String,
    ): ProfileService {
    override fun getAllProfiles(): List<ProfileDTO> {
        return profileRepository.findAll().map { it.toDTO() }
    }

    override fun getProfile(email: String): ProfileDTO? {
        val dbProfile = profileRepository.findByEmail(email)?.toDTO()
        if(dbProfile != null)
            return dbProfile
        else
            throw ProfileNotFoundException("No profile with mail: $email found.")
    }

    override fun getProfileById(profileId: String): ProfileDTO? {
        return profileRepository.findByProfileId(profileId)?.toDTO()
    }



    override fun createNewProfile(profileDTO: ProfileDTO): ProfileDTO? {
        val profile = Profile.fromDTO(profileDTO)
        val dbProfile = profileRepository.findByEmail(profile.email)?.toDTO()
        if (dbProfile != null) {
            throw DuplicateProfileException("User with mail: ${profile.email} already exists.")
        }else{
            profileRepository.save(profile)
            return profile.toDTO()
        }
    }

    override fun updateProfile(email: String, profileDTO: ProfileDTO): ProfileDTO? {
        profileRepository.findByEmailOrEmailNull(email)
            .map {
                it.profileId = profileDTO.profileId
                it.name = profileDTO.name
                it.surname = profileDTO.surname
                it.phonenumber = profileDTO.phonenumber
                it.birthdate = profileDTO.birthdate
                profileRepository.save(it)
            }.orElseThrow(){
                throw ProfileNotFoundException("User with mail: $email does not exist.")
            }
        return profileDTO
    }
}
