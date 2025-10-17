package com.memory.config.jwt

import com.memory.component.jwt.JwtComponent
import com.memory.exception.customException.JwtException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*

@Component
@ConditionalOnProperty(prefix = "jwt.token", name = ["enabled"], havingValue = "true")
class JwtTokenProvider(
    private val jwtComponent: JwtComponent
) {
    private lateinit var key: Key
    private val signatureAlgorithm = SignatureAlgorithm.HS256

    @PostConstruct
    fun init() {
        key = Keys.hmacShaKeyFor(jwtComponent.secret.toByteArray())
    }

    fun createAccessToken(subject: String): String =
        createToken(subject, jwtComponent.expiration?.times(1000L) ?: 0L)

    fun createRefreshToken(subject: String): String =
        createToken(subject, jwtComponent.refresh?.times(1000L) ?: 0L)

    private fun createToken(subject: String, expirationTimeMillis: Long): String {
        val claims: Claims = Jwts.claims().setSubject(subject)
        val now = Date()
        val validity = Date(now.time + expirationTimeMillis)

        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(now)
            .setExpiration(validity)
            .signWith(key, signatureAlgorithm)
            .compact()
    }

    fun getSubject(token: String): String {
        return try {
            Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body
                .subject
        } catch (e: Exception) {
            throw JwtException("올바르지 않은 JWT 토큰입니다.")
        }
    }
}
