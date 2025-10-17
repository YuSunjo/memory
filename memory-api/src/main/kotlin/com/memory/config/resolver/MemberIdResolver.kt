package com.memory.config.resolver;

import com.memory.annotation.MemberId;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.exception.customException.JwtException;
import com.memory.exception.customException.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class MemberIdResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MemberId.class) && 
               parameter.getParameterType().equals(Long.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                 NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        
        String token = (String) request.getAttribute("token");
        if (!StringUtils.hasText(token)) {
            token = extractToken(request);
            if (!StringUtils.hasText(token)) {
                throw new JwtException("JWT 토큰이 없습니다.");
            }
        }
        
        String email = jwtTokenProvider.getSubject(token);
        if (!StringUtils.hasText(email)) {
            throw new JwtException("유효하지 않은 JWT 토큰입니다.");
        }
        
        Member member = memberRepository.findMemberByEmailAndMemberType(email, MemberType.MEMBER)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
        
        return member.getId();
    }
    
    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}