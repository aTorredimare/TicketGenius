package it.polito.waii.ticketingserver.profile

import io.micrometer.observation.annotation.Observed
import it.polito.waii.ticketingserver.exceptions.DuplicateProfileException
import it.polito.waii.ticketingserver.exceptions.ProfileNotFoundException
import it.polito.waii.ticketingserver.profile.dtos.ProfileDTO
import it.polito.waii.ticketingserver.profile.services.ProfileService
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Observed
class ProfileController(private val profileService: ProfileService) {
    @GetMapping("/API/profiles")
    fun getAllProfiles(): List<ProfileDTO>{
        return profileService.getAllProfiles()
    }

    @GetMapping("/API/profiles/mail/{email}")
    fun getProfile(@PathVariable email: String): ProfileDTO? {
        return profileService.getProfile(email)
    }

    @GetMapping("/API/profiles/id/{profileId}")
    fun getProfileById(@PathVariable profileId: String) : ProfileDTO? {
        return  profileService.getProfileById(profileId)
    }

    @PostMapping("/API/profiles")
    fun createNewProfile(@Valid @RequestBody profileDTO: ProfileDTO): ProfileDTO? {
        try {
            return profileService.createNewProfile(profileDTO)
        } catch (ex: DuplicateProfileException) {
            throw ex
        }
    }

    @PutMapping("/API/profiles/{email}")
    fun updateProfile(@Valid @RequestBody profileDTO: ProfileDTO, @PathVariable email: String): ProfileDTO?{
        //print(email)
        try {
            return profileService.updateProfile(email, profileDTO)
        }catch (ex: ProfileNotFoundException){
            throw ex
        }
    }
}
