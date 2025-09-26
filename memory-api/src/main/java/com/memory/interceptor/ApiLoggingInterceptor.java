package com.memory.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApiLoggingInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper;
    private static final String REQUEST_TIME_ATTRIBUTE = "requestTime";
    private static final String TRACE_ID_ATTRIBUTE = "traceId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime = System.currentTimeMillis();
        String traceId = UUID.randomUUID().toString().substring(0, 8);

        request.setAttribute(REQUEST_TIME_ATTRIBUTE, startTime);
        request.setAttribute(TRACE_ID_ATTRIBUTE, traceId);

        MDC.put("traceId", traceId);
        MDC.put("method", request.getMethod());
        MDC.put("uri", request.getRequestURI());
        MDC.put("userAgent", request.getHeader("User-Agent"));
        MDC.put("remoteAddr", getClientIpAddress(request));

        if (handler instanceof HandlerMethod handlerMethod) {
            String controllerName = handlerMethod.getBeanType().getSimpleName();
            String methodName = handlerMethod.getMethod().getName();
            MDC.put("controller", controllerName);
            MDC.put("action", methodName);
        }

        Map<String, Object> logData = new HashMap<>();
        logData.put("type", "API_REQUEST");
        logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        logData.put("traceId", traceId);
        logData.put("method", request.getMethod());
        logData.put("uri", request.getRequestURI());
        logData.put("queryString", request.getQueryString());
        logData.put("userAgent", request.getHeader("User-Agent"));
        logData.put("remoteAddr", getClientIpAddress(request));
        logData.put("headers", getHeaders(request));

        if (handler instanceof HandlerMethod handlerMethod) {
            logData.put("controller", handlerMethod.getBeanType().getSimpleName());
            logData.put("method", handlerMethod.getMethod().getName());
        }

        log.info("API Request: {}", objectMapper.writeValueAsString(logData));

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                          ModelAndView modelAndView) throws Exception {
        // 특별한 처리가 필요한 경우 여기에 구현
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                               Exception ex) throws Exception {
        try {
            long startTime = (Long) request.getAttribute(REQUEST_TIME_ATTRIBUTE);
            String traceId = (String) request.getAttribute(TRACE_ID_ATTRIBUTE);
            long endTime = System.currentTimeMillis();
            long processingTime = endTime - startTime;

            Map<String, Object> logData = new HashMap<>();
            logData.put("type", "API_RESPONSE");
            logData.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            logData.put("traceId", traceId);
            logData.put("method", request.getMethod());
            logData.put("uri", request.getRequestURI());
            logData.put("statusCode", response.getStatus());
            logData.put("processingTime", processingTime);
            logData.put("success", response.getStatus() < 400);

            if (ex != null) {
                logData.put("exception", ex.getClass().getSimpleName());
                logData.put("errorMessage", ex.getMessage());
                log.error("API Response with Exception: {}", objectMapper.writeValueAsString(logData), ex);
            } else {
                if (response.getStatus() >= 400) {
                    log.warn("API Response: {}", objectMapper.writeValueAsString(logData));
                } else {
                    log.info("API Response: {}", objectMapper.writeValueAsString(logData));
                }
            }
        } finally {
            MDC.clear();
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }

    private Map<String, String> getHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            // 민감한 정보는 로그에서 제외
            if (!headerName.toLowerCase().contains("authorization") &&
                !headerName.toLowerCase().contains("cookie")) {
                headers.put(headerName, request.getHeader(headerName));
            }
        }

        return headers;
    }
}