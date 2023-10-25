package it.polito.waii.ticketingserver.observation.aop

import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect


@Aspect
class DefaultLogAspect : AbstractLogAspect() {
    @Around(
        "@annotation(org.example.event.sourcing.order.poc.common.annotation.LogInfo)" +
        "|| @within(org.example.event.sourcing.order.poc.common.annotation.LogInfo)"
    )
    @Throws(Throwable::class)
    override fun logInfoAround(joinPoint: ProceedingJoinPoint): Any {
        return super.logInfoAround(joinPoint)
    }
}