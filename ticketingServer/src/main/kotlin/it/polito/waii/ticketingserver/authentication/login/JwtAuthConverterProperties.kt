package it.polito.waii.ticketingserver.authentication.login

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration


@Configuration
class JwtAuthConverterProperties {
//    @Value("\${jwt.auth.converter.resource-id}")
//    lateinit var resourceId: String
//    @Value("\${jwt.auth.converter.principal-attribute}")
//    lateinit var principalAttribute: String
    val principalAttribute = "preferred_username"
}