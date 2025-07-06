package com.memory.config.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Slf4j
@Aspect
@Component
public class ControllerLoggingAspect {

    @Before("execution(* com.memory.controller..*(..))")
    public void logControllerEntry(JoinPoint joinPoint) {
        HttpServletRequest request = getHttpServletRequest();
        if (request != null) {
            String method = request.getMethod();
            String url = request.getRequestURL().toString();
            String clientIp = getClientIp(request);
            
            log.info("🎯 [CONTROLLER] {} {} - Class: {}, Method: {}, IP: {}", 
                method, url, 
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                clientIp
            );
            
            // 파라미터 로그 (민감정보 제외)
            Object[] args = joinPoint.getArgs();
            if (args.length > 0) {
                log.info("📝 [PARAMS] {}", Arrays.toString(args));
            }
        }
    }

    @Around("execution(* com.memory.controller..*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        
        try {
            Object result = joinPoint.proceed();
            long endTime = System.currentTimeMillis();
            
            log.info("⏱️ [TIMING] {}.{} executed in {}ms", 
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                (endTime - startTime)
            );
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("❌ [ERROR] {}.{} failed after {}ms - Error: {}", 
                joinPoint.getTarget().getClass().getSimpleName(),
                joinPoint.getSignature().getName(),
                (endTime - startTime),
                e.getMessage()
            );
            throw e;
        }
    }

    @AfterReturning(pointcut = "execution(* com.memory.controller..*(..))", returning = "result")
    public void logControllerReturn(JoinPoint joinPoint, Object result) {
        log.info("✅ [SUCCESS] {}.{} completed successfully", 
            joinPoint.getTarget().getClass().getSimpleName(),
            joinPoint.getSignature().getName()
        );
    }

    private HttpServletRequest getHttpServletRequest() {
        try {
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            return attributes.getRequest();
        } catch (Exception e) {
            return null;
        }
    }

    private String getClientIp(HttpServletRequest request) {
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
}
