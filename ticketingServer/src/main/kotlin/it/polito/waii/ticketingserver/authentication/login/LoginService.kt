package it.polito.waii.ticketingserver.authentication.login

interface LoginService {
    fun getUserToken(email: String, password: String): Map<String, String>?
    fun refreshUserToken(refreshTokenDTO: RefreshTokenDTO): Map<String, String>?

}