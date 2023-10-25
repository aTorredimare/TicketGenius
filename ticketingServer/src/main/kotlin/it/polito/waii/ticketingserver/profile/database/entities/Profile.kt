package it.polito.waii.ticketingserver.profile.database.entities

import it.polito.waii.ticketingserver.profile.dtos.ProfileDTO
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name="profiles")
class Profile(
    @Id
    var profileId: String,
    var email: String,
    var name: String,
    var surname: String,
    var birthdate: LocalDate,
    var phonenumber: String
) {

    companion object {
        fun fromDTO(profileDTO: ProfileDTO): Profile {
            return Profile(
                profileDTO.profileId,
                profileDTO.email,
                profileDTO.name,
                profileDTO.surname,
                profileDTO.birthdate,
                profileDTO.phonenumber
            )
        }
    }
}
