package com.memory.component.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt.token")
@Getter
@Setter
@ConditionalOnProperty(prefix = "jwt.token", name = "enabled", havingValue = "true")
public class JwtComponent {

    private String secret;
    private Long expiration;
    private Long refresh;

}
