package it.polito.waii.ticketingserver.authentication.signup

import it.polito.waii.ticketingserver.authentication.dtos.SignUpExpertDTO
import it.polito.waii.ticketingserver.authentication.dtos.SignUpUserDTO
import it.polito.waii.ticketingserver.profile.dtos.ProfileDTO
import it.polito.waii.ticketingserver.ticket.dtos.EmployeeDTO
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class SignUpController(
    private val signupService: SignupService
) {
    @PostMapping("/API/signup")
    fun signup(@RequestBody @Valid signUpUserDTO: SignUpUserDTO): ProfileDTO? {
        return signupService.addUser(signUpUserDTO)
    }

    @PostMapping("/API/createexpert")
    fun createExpert(@RequestBody @Valid signUpExpertDTO: SignUpExpertDTO): EmployeeDTO? {
        return signupService.addExpert(signUpExpertDTO)
    }
}