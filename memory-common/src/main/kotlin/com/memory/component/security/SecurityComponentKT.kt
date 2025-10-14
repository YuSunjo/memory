package com.memory.component.security

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "cors")
@ConditionalOnProperty(prefix = "cors", name = ["enabled"], havingValue = "true")
class SecurityComponentKT {
    var allowedOrigins: List<String> = emptyList()
}
