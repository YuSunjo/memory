package com.memory.config

import com.memory.config.interceptor.AdminInterceptor
import com.memory.config.interceptor.AuthInterceptor
import com.memory.config.resolver.MemberIdResolver
import com.memory.interceptor.ApiLoggingInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val memberIdResolver: MemberIdResolver,
    private val javaAuthInterceptor: AuthInterceptor,
    private val javaAdminInterceptor: AdminInterceptor,
    private val apiLoggingInterceptor: ApiLoggingInterceptor
) : WebMvcConfigurer {

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.add(memberIdResolver)
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(apiLoggingInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**")

        registry.addInterceptor(javaAuthInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**")

        registry.addInterceptor(javaAdminInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns("/swagger-ui/**", "/v3/api-docs/**")
    }

    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {
        registry.addResourceHandler("/swagger-ui/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/swagger-ui/")
            .resourceChain(false)

        registry.addResourceHandler("/swagger-ui.html")
            .addResourceLocations("classpath:/META-INF/resources/swagger-ui/index.html")
            .resourceChain(false)

        registry.addResourceHandler("/webjars/**")
            .addResourceLocations("classpath:/META-INF/resources/webjars/")
            .resourceChain(false)
    }
}
