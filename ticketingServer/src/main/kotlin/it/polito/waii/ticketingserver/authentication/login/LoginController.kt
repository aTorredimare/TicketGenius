package it.polito.waii.ticketingserver.authentication.login

import com.fasterxml.jackson.annotation.JsonProperty
import io.micrometer.observation.annotation.Observed
import jakarta.validation.constraints.NotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

data class UserLoginInfo(val email: String, val password: String)
data class RefreshTokenDTO(@JsonProperty("refresh_token")@field:NotNull val refreshToken: String)

@RestController
@Observed
class LoginController (private val loginService: LoginService) {
    companion object {
        val log: Logger = LoggerFactory.getLogger(LoginController::class.java)
    }
    @PostMapping("/API/login")
    fun getUserToken(@RequestBody userLoginInfo: UserLoginInfo): Map<String, String>? {
        log.info("receive login request, credentials = $userLoginInfo.")
        return loginService.getUserToken(userLoginInfo.email, userLoginInfo.password)
    }

    @PostMapping("API/token/refresh")
    fun refreshUserToken(@RequestBody refreshTokenDTO: RefreshTokenDTO): Map<String, String>? {
        log.info("receive access token, jwt = $refreshTokenDTO.")
        return loginService.refreshUserToken(refreshTokenDTO)
    }
}
