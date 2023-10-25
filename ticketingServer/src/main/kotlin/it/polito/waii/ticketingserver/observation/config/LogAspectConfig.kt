package it.polito.waii.ticketingserver.observation.config

import it.polito.waii.ticketingserver.observation.aop.AbstractLogAspect
import it.polito.waii.ticketingserver.observation.aop.DefaultLogAspect
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration


@Configuration(proxyBeanMethods = false)
class LogAspectConfig {
    @Bean
    @ConditionalOnMissingBean
    fun defaultLogAspect(): AbstractLogAspect {
        return DefaultLogAspect()
    }
}