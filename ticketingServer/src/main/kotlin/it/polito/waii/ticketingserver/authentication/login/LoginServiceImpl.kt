package it.polito.waii.ticketingserver.authentication.login

import it.polito.waii.ticketingserver.exceptions.KeyCloakGenericException
import it.polito.waii.ticketingserver.exceptions.KeyCloakLoginException
import it.polito.waii.ticketingserver.observation.annotation.LogInfo
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate
@Service
@LogInfo
class LoginServiceImpl: LoginService {
    companion object {
        val log: Logger = LoggerFactory.getLogger(LoginServiceImpl::class.java)
    }

    @Value("\${spring.security.oauth2.resourceserver.jwt.token-uri}")
    private lateinit var uri: String
    override fun getUserToken(email: String, password: String): Map<String, String>? {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val loginData = LinkedMultiValueMap<String,String>()
        loginData.add("username", email)
        loginData.add("password",password)
        loginData.add("grant_type","password")
        loginData.add("client_id", "springboot-keycloak-client")

        val requestBody = HttpEntity(loginData, headers)
        
        return try {
            val response = restTemplate.postForEntity(uri, requestBody, LinkedHashMap::class.java)
            log.info("successfully logged in, access token = ${response.body?.get("access_token")}")
            response.body?.filterKeys { k -> k.equals("access_token") || k.equals("refresh_token") } as? Map<String, String>
        } catch (e: HttpStatusCodeException) {
            log.warn("login failed, status code = ${e.statusCode}")
            if(e.statusCode == HttpStatus.UNAUTHORIZED)
                throw KeyCloakGenericException("Error on login, verify inserted email and password")
            else
                throw KeyCloakLoginException("Error on login, server unreachable")
        }
    }

    override fun refreshUserToken(refreshTokenDTO: RefreshTokenDTO): Map<String, String>? {
        val restTemplate = RestTemplate()
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED

        val refreshData = LinkedMultiValueMap<String,String>()
        refreshData.add("client_id", "springboot-keycloak-client")
        refreshData.add("grant_type","refresh_token")
        refreshData.add("refresh_token", refreshTokenDTO.refreshToken)

        val requestBody = HttpEntity(refreshData, headers)

        return try {
            val response = restTemplate.postForEntity(uri, requestBody, LinkedHashMap::class.java)
            log.info("successfully refreshed token, access token = ${response.body?.get("access_token")}")
            response.body?.filterKeys { k -> k.equals("access_token") || k.equals("refresh_token") } as? Map<String, String>
        } catch (e: HttpStatusCodeException) {
            log.warn("token refresh failed, status code = ${e.statusCode}")
            if(e.statusCode == HttpStatus.BAD_REQUEST)
                throw KeyCloakGenericException("Error on token refresh, login again.")
            else
                throw KeyCloakLoginException("Error on token refresh, server unreachable.")
        }
    }
}
