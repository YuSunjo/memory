package com.memory.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

@Component
class ApiLoggingInterceptor(
    private val objectMapper: ObjectMapper
) : HandlerInterceptor {

    private val log = LoggerFactory.getLogger(ApiLoggingInterceptor::class.java)

    companion object {
        private const val REQUEST_TIME_ATTRIBUTE = "requestTime"
        private const val TRACE_ID_ATTRIBUTE = "traceId"
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        val startTime = System.currentTimeMillis()
        val traceId = UUID.randomUUID().toString().substring(0, 8)

        request.setAttribute(REQUEST_TIME_ATTRIBUTE, startTime)
        request.setAttribute(TRACE_ID_ATTRIBUTE, traceId)

        MDC.put("traceId", traceId)
        MDC.put("method", request.method)
        MDC.put("uri", request.requestURI)
        MDC.put("userAgent", request.getHeader("User-Agent"))
        MDC.put("remoteAddr", getClientIpAddress(request))

        if (handler is HandlerMethod) {
            val controllerName = handler.beanType.simpleName
            val methodName = handler.method.name
            MDC.put("controller", controllerName)
            MDC.put("action", methodName)
        }

        val logData = mutableMapOf<String, Any?>(
            "type" to "API_REQUEST",
            "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            "traceId" to traceId,
            "method" to request.method,
            "uri" to request.requestURI,
            "queryString" to request.queryString,
            "userAgent" to request.getHeader("User-Agent"),
            "remoteAddr" to getClientIpAddress(request),
            "headers" to getHeaders(request)
        )

        if (handler is HandlerMethod) {
            logData["controller"] = handler.beanType.simpleName
            logData["method"] = handler.method.name
        }

        log.info("API Request: {}", objectMapper.writeValueAsString(logData))

        return true
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?
    ) {
    }

    override fun afterCompletion(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        ex: Exception?
    ) {
        try {
            val startTime = request.getAttribute(REQUEST_TIME_ATTRIBUTE) as Long
            val traceId = request.getAttribute(TRACE_ID_ATTRIBUTE) as String
            val endTime = System.currentTimeMillis()
            val processingTime = endTime - startTime

            val logData = mutableMapOf<String, Any?>(
                "type" to "API_RESPONSE",
                "timestamp" to LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                "traceId" to traceId,
                "method" to request.method,
                "uri" to request.requestURI,
                "statusCode" to response.status,
                "processingTime" to processingTime,
                "success" to (response.status < 400)
            )

            if (ex != null) {
                logData["exception"] = ex.javaClass.simpleName
                logData["errorMessage"] = ex.message
                log.error("API Response with Exception: {}", objectMapper.writeValueAsString(logData), ex)
            } else {
                if (response.status >= 400) {
                    log.warn("API Response: {}", objectMapper.writeValueAsString(logData))
                } else {
                    log.info("API Response: {}", objectMapper.writeValueAsString(logData))
                }
            }
        } finally {
            MDC.clear()
        }
    }

    private fun getClientIpAddress(request: HttpServletRequest): String {
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

    private fun getHeaders(request: HttpServletRequest): Map<String, String> {
        val headers = mutableMapOf<String, String>()
        val headerNames = request.headerNames

        while (headerNames.hasMoreElements()) {
            val headerName = headerNames.nextElement()
            if (!headerName.lowercase().contains("authorization") &&
                !headerName.lowercase().contains("cookie")
            ) {
                headers[headerName] = request.getHeader(headerName)
            }
        }

        return headers
    }
}