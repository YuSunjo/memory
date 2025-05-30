package com.memory.config.jwt;

import com.memory.component.jwt.JwtComponent;
import com.memory.exception.customException.JwtException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtComponent jwtComponent;
    private Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtComponent.getSecret().getBytes());
    }

    public String createAccessToken(String subject) {
        return createToken(subject, jwtComponent.getExpiration() * 1000);
    }

    public String createRefreshToken(String subject) {
        return createToken(subject, jwtComponent.getRefresh() * 1000);
    }

    private String createToken(String subject, long expirationTimeMillis) {
        Claims claims = Jwts.claims().setSubject(subject);
        Date now = new Date();
        Date validity = new Date(now.getTime() + expirationTimeMillis);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(validity)
                .signWith(key, signatureAlgorithm)
                .compact();
    }

    public String getSubject(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new JwtException("올바르지 않은 JWT 토큰입니다.");
        }
    }

}
