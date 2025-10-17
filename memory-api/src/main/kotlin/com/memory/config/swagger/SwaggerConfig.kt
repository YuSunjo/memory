package com.memory.config.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfigKT {

    @Bean
    fun memoryApi(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .group("memory-api")
            .pathsToMatch("/api/**")
            .build()

    @Bean
    fun openAPI(): OpenAPI {
        val info = Info()
            .title("Memory API")
            .version("v1.0")
            .description("Memory API Documentation")

        val securityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name("Authorization")

        val server = Server()
            .url("/")
            .description("Default Server URL")

        return OpenAPI()
            .openapi("3.0.1")
            .info(info)
            .addServersItem(server)
            .components(Components().addSecuritySchemes("bearerAuth", securityScheme))
    }
}