package com.memory.config.aop

import jakarta.servlet.http.HttpServletRequest
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.*
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Aspect
@Component
class ControllerLoggingAspect {
    private val log = LoggerFactory.getLogger(ControllerLoggingAspect::class.java)

    @Before("execution(* com.memory.controller..*(..))")
    fun logControllerEntry(joinPoint: JoinPoint) {
        val request = getHttpServletRequest()
        if (request != null) {
            val method = request.method
            val url = request.requestURL.toString()
            val clientIp = getClientIp(request)

            log.info(
                "🎯 [CONTROLLER] {} {} - Class: {}, Method: {}, IP: {}",
                method, url,
                joinPoint.target.javaClass.simpleName,
                joinPoint.signature.name,
                clientIp
            )

            val args = joinPoint.args
            if (args.isNotEmpty()) {
                log.info("📝 [PARAMS] {}", args.contentToString())
            }
        }
    }

    @Around("execution(* com.memory.controller..*(..))")
    fun logExecutionTime(joinPoint: ProceedingJoinPoint): Any? {
        val startTime = System.currentTimeMillis()

        return try {
            val result = joinPoint.proceed()
            val endTime = System.currentTimeMillis()

            log.info(
                "⏱️ [TIMING] {}.{} executed in {}ms",
                joinPoint.target.javaClass.simpleName,
                joinPoint.signature.name,
                endTime - startTime
            )

            result
        } catch (e: Exception) {
            val endTime = System.currentTimeMillis()
            log.error(
                "❌ [ERROR] {}.{} failed after {}ms - Error: {}",
                joinPoint.target.javaClass.simpleName,
                joinPoint.signature.name,
                endTime - startTime,
                e.message
            )
            throw e
        }
    }

    @AfterReturning(pointcut = "execution(* com.memory.controller..*(..))", returning = "result")
    fun logControllerReturn(joinPoint: JoinPoint, result: Any?) {
        log.info(
            "✅ [SUCCESS] {}.{} completed successfully",
            joinPoint.target.javaClass.simpleName,
            joinPoint.signature.name
        )
    }

    private fun getHttpServletRequest(): HttpServletRequest? {
        return try {
            val attributes = RequestContextHolder.currentRequestAttributes() as ServletRequestAttributes
            attributes.request
        } catch (e: Exception) {
            null
        }
    }

    private fun getClientIp(request: HttpServletRequest): String {
        val xForwardedFor = request.getHeader("X-Forwarded-For")
        if (!xForwardedFor.isNullOrEmpty()) {
            return xForwardedFor.split(",")[0].trim()
        }

        val xRealIp = request.getHeader("X-Real-IP")
        if (!xRealIp.isNullOrEmpty()) {
            return xRealIp
        }

        return request.remoteAddr
    }
}
