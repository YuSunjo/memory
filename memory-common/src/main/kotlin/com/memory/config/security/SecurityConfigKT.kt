package com.memory.config.security

import com.memory.component.security.SecurityComponentKT
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfigKT(
    private val securityComponent: SecurityComponentKT
) {

    private val log = LoggerFactory.getLogger(SecurityConfigKT::class.java)

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web ->
            web.ignoring()
                .requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**", "/favicon.ico", "/error", "/actuator/**")
        }
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration = CorsConfiguration()

        log.info("Allowed origins: {}", securityComponent.allowedOrigins)
        configuration.allowedOriginPatterns = securityComponent.allowedOrigins

        configuration.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        configuration.allowedHeaders = listOf("*")
        configuration.allowCredentials = true
        configuration.maxAge = 3600L

        configuration.exposedHeaders = listOf(
            "Authorization",
            "Content-Type",
            "X-Requested-With",
            "accept",
            "Origin",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        )

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", configuration)
        return source
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.cors { cors -> cors.configurationSource(corsConfigurationSource()) }
            .csrf { it.disable() }
            .formLogin { it.disable() }
            .authorizeHttpRequests { auth ->
                auth.anyRequest().permitAll()
            }

        return http.build()
    }
}
