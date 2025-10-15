package com.memory.component.jwt

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "jwt.token")
@ConditionalOnProperty(prefix = "jwt.token", name = ["enabled"], havingValue = "true")
class JwtComponent {
    lateinit var secret: String
    var expiration: Long? = null
    var refresh: Long? = null
}