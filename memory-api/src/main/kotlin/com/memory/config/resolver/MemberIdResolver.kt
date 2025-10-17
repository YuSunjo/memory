package com.memory.config.resolver

import com.memory.annotation.MemberId
import com.memory.config.jwt.JwtTokenProvider
import com.memory.domain.member.MemberType
import com.memory.domain.member.repository.MemberRepository
import com.memory.exception.customException.JwtException
import com.memory.exception.customException.NotFoundException
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class MemberIdResolver(
    private val jwtTokenProvider: JwtTokenProvider,
    private val memberRepository: MemberRepository
) : HandlerMethodArgumentResolver {

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        parameter.hasParameterAnnotation(MemberId::class.java) &&
            parameter.parameterType == Long::class.javaObjectType

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.nativeRequest as HttpServletRequest

        var token = request.getAttribute("token") as? String
        if (!StringUtils.hasText(token)) {
            token = extractToken(request)
            if (!StringUtils.hasText(token)) {
                throw JwtException("JWT 토큰이 없습니다.")
            }
        }

        val email = jwtTokenProvider.getSubject(token!!)
        if (!StringUtils.hasText(email)) {
            throw JwtException("유효하지 않은 JWT 토큰입니다.")
        }

        val member = memberRepository.findMemberByEmailAndMemberType(email, MemberType.MEMBER)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        return member.id!!
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