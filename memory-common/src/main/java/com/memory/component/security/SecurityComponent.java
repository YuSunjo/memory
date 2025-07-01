package com.memory.component.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "cors")
@Getter
@Setter
@ConditionalOnProperty(prefix = "cors", name = "enabled", havingValue = "true")
public class SecurityComponent {

    private List<String> allowedOrigins;

}
