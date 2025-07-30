package com.memory.config.interceptor;

import com.memory.annotation.Admin;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.exception.customException.JwtException;
import com.memory.exception.customException.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
@RequiredArgsConstructor
public class AdminInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }

        Admin admin = handlerMethod.getMethodAnnotation(Admin.class);

        if (admin == null) {
            return true;
        }

        String token = extractToken(request);
        if (!StringUtils.hasText(token)) {
            throw new JwtException("JWT 토큰이 없습니다.");
        }

        String email = jwtTokenProvider.getSubject(token);
        if (!StringUtils.hasText(email)) {
            throw new JwtException("유효하지 않은 JWT 토큰입니다.");
        }

        memberRepository.findMemberByEmailAndMemberType(email, MemberType.ADMIN)
                .orElseThrow(() -> new NotFoundException("관리자를 찾을 수 없습니다."));
        return true;
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}