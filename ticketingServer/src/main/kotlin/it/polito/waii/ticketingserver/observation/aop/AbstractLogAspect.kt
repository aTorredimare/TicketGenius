package it.polito.waii.ticketingserver.observation.aop

import jakarta.annotation.Nullable
import jakarta.validation.constraints.NotNull
import org.aspectj.lang.ProceedingJoinPoint
import org.slf4j.Logger
import org.slf4j.LoggerFactory


open class AbstractLogAspect {
    @Throws(Throwable::class)
    open fun logInfoAround(joinPoint: ProceedingJoinPoint): Any {
        val logInfo = getLogInfo(joinPoint)
        val log = LoggerFactory.getLogger(logInfo.declaringType)
        logBefore(logInfo, log)
        val any = joinPoint.proceed()
        logAfter(logInfo, log)
        return any
    }

    fun logBefore(joinPoint: ProceedingJoinPoint) {
        val logInfo = getLogInfo(joinPoint)
        val log = LoggerFactory.getLogger(logInfo.declaringType)
        logBefore(logInfo, log)
    }

    fun logAfter(joinPoint: ProceedingJoinPoint) {
        val logInfo = getLogInfo(joinPoint)
        val log = LoggerFactory.getLogger(logInfo.declaringType)
        logAfter(logInfo, log)
    }

    @JvmRecord
    @SuppressWarnings
    private data class LogInfo(
        val declaringType: @NotNull Class<*>,
        val className: @NotNull String,
        val annotatedMethodName: @NotNull String,
        @field:Nullable @param:Nullable val args: Array<Any>
    )

    companion object {
        private fun getLogInfo(joinPoint: ProceedingJoinPoint): LogInfo {
            val signature = joinPoint.signature
            val declaringType = signature.declaringType
            val className = declaringType.simpleName
            val annotatedMethodName = signature.name
            val args = joinPoint.args
            return LogInfo(declaringType, className, annotatedMethodName, args)
        }

        private fun logBefore(logInfo: LogInfo, log: Logger) {
            log.info("[{}.{}] start ({})", logInfo.className, logInfo.annotatedMethodName, logInfo.args)
        }

        private fun logAfter(logInfo: LogInfo, log: Logger) {
            log.info("[{}.{}] end", logInfo.className, logInfo.annotatedMethodName)
        }
    }
}