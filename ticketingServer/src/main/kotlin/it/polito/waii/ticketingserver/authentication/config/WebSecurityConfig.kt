package it.polito.waii.ticketingserver.authentication.config

import it.polito.waii.ticketingserver.authentication.login.JwtAuthConverter
import it.polito.waii.ticketingserver.authentication.login.JwtAuthConverterProperties
import lombok.RequiredArgsConstructor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
class WebSecurityConfig {
    private val jwtAuthConverter = JwtAuthConverter(JwtAuthConverterProperties())
    companion object {
        val AUTH_WHITELIST = arrayOf("/API/**", "/actuator/prometheus")
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors().disable()
            .csrf().disable()
            .authorizeHttpRequests()
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers("/index.html","/", "/static/**", "/manifest.json", "/signup").permitAll()
            .requestMatchers("/API/login").permitAll()
            .requestMatchers("/API/signup").permitAll()
            .requestMatchers(HttpMethod.GET, "/API/token/refresh").authenticated()
            .requestMatchers(HttpMethod.POST, "/API/createexpert").hasAnyRole("app_manager")
            .requestMatchers(HttpMethod.GET, "/API/profiles").hasAnyRole("app_expert", "app_manager")
            .requestMatchers(HttpMethod.POST,"/API/profiles").authenticated()
            .requestMatchers(HttpMethod.PUT,"/API/profiles").authenticated()
            .requestMatchers(HttpMethod.GET, "/API/profiles/{email}").authenticated()

            .requestMatchers(HttpMethod.POST, "/API/ticket").hasRole("app_client")
            .requestMatchers(HttpMethod.PUT, "/API/ticket/assign/{ticketId}").hasRole("app_manager")
            .requestMatchers(HttpMethod.PUT, "/API/ticket/changestatus/{ticketId}").hasAnyRole("app_manager", "app_expert")
            .requestMatchers(HttpMethod.GET, "/API/tickets/**").authenticated()

            .requestMatchers("/API/tickethistory/currentstatus/{ticketId}").authenticated()
            .requestMatchers("/API/tickethistory/**").hasAnyRole("app_expert", "app_manager")

            .requestMatchers("/API/sales/**").authenticated()

            .requestMatchers("/API/products/customer/{customerId}").hasRole("app_client")
            .requestMatchers("/API/products/**").permitAll()
            .requestMatchers("/API/test").hasAnyRole("app_client", "app_manager")

            .requestMatchers("/API/ticket/{ticketId}/chat").authenticated()
            .requestMatchers("/API/ticket/newmessage").authenticated()
            .requestMatchers("/API/ticket/{tickedID}/chat/{messageId}/{attachmentId}").authenticated()

            .requestMatchers("/API/employees/id/{employeeId}").hasAnyRole("app_expert", "app_manager")

            .requestMatchers(*AUTH_WHITELIST).permitAll()
            //.anyRequest().authenticated()
            //login and logout forms will be implemented by client
            //.and().formLogin().permitAll()
            //.and().logout().permitAll()

        http.oauth2ResourceServer()
            .jwt()
            .jwtAuthenticationConverter(jwtAuthConverter)

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        return http.build()
    }
}
