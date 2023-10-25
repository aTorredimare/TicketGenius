package it.polito.waii.ticketingserver.debug

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.CommonsRequestLoggingFilter

@Configuration
class RequestLoggingFilterConfig {
    @Bean
    fun logFilter(): CommonsRequestLoggingFilter {
        val filter = CommonsRequestLoggingFilter()

        filter.setIncludeHeaders(false)
        filter.setIncludeQueryString(true)
        filter.setIncludePayload(true)
        filter.setAfterMessagePrefix("REQUEST DATA: ")
        return filter
    }
}