package it.polito.waii.ticketingserver.authentication.login


import org.springframework.core.convert.converter.Converter
import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.stereotype.Component
import java.util.stream.Collectors
import java.util.stream.Stream


@Component
class JwtAuthConverter(private val properties: JwtAuthConverterProperties) :
    Converter<Jwt, AbstractAuthenticationToken> {
    private val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
    override fun convert(value: Jwt): AbstractAuthenticationToken? {
        val authorities: Collection<GrantedAuthority> = Stream.concat(
            jwtGrantedAuthoritiesConverter.convert(value)!!.stream(),
            extractResourceRoles(value).stream()
        ).collect(Collectors.toSet())
        return JwtAuthenticationToken(value, authorities, getPrincipalClaimName(value))
    }

    private fun getPrincipalClaimName(jwt: Jwt): String {
        return jwt.getClaim(properties.principalAttribute)
    }

    private fun extractResourceRoles(jwt: Jwt): Collection<GrantedAuthority> {
        println(jwt)
        val realmAccess: Map<String, Any> = jwt.getClaim("realm_access") ?: return setOf()
        val roles = realmAccess["roles"] as? Collection<String> ?: return setOf()

        return roles.map { role: String ->
            SimpleGrantedAuthority("ROLE_$role")
        }.toSet()

    }
}