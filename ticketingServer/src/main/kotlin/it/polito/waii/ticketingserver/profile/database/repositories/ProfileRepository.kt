package it.polito.waii.ticketingserver.profile.database.repositories

import it.polito.waii.ticketingserver.profile.database.entities.Profile
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface ProfileRepository: JpaRepository<Profile, String> {
    fun findByEmail(email: String) : Profile?
    fun findByProfileId(profileId: String) : Profile?
    fun findByEmailOrEmailNull(email: String) : Optional<Profile>
}