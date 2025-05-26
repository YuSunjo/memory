package com.memory.component.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt.token")
@Getter
@Setter
public class JwtComponent {

    private String secret;
    private Long expiration;
    private Long refresh;

}
