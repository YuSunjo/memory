package com.memory.config.interceptor

import com.memory.annotation.Auth
import com.memory.config.jwt.JwtTokenProvider
import com.memory.exception.customException.JwtException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor

@Component
class AuthInterceptor(
    private val jwtTokenProvider: JwtTokenProvider
) : HandlerInterceptor {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {
        if (handler !is HandlerMethod) {
            return true
        }

        handler.getMethodAnnotation(Auth::class.java) ?: return true

        val token = extractToken(request)
        if (!StringUtils.hasText(token)) {
            throw JwtException("JWT 토큰이 없습니다.")
        }

        val subject = jwtTokenProvider.getSubject(token!!)
        if (!StringUtils.hasText(subject)) {
            throw JwtException("유효하지 않은 JWT 토큰입니다.")
        }

        request.setAttribute("token", token)
        return true
    }

    private fun extractToken(request: HttpServletRequest): String? {
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        return if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            bearerToken.substring(BEARER_PREFIX.length)
        } else {
            null
        }
    }
}
