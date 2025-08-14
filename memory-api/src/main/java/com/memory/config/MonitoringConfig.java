package com.memory.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringConfig {

    @Bean
    public Counter memoryCreationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("memory.creation.count")
                .description("Number of memories created")
                .register(meterRegistry);
    }

    @Bean
    public Counter memberRegistrationCounter(MeterRegistry meterRegistry) {
        return Counter.builder("member.registration.count")
                .description("Number of members registered")
                .register(meterRegistry);
    }

    @Bean
    public Timer fileUploadTimer(MeterRegistry meterRegistry) {
        return Timer.builder("file.upload.duration")
                .description("File upload processing time")
                .register(meterRegistry);
    }

    @Bean
    public Counter databaseErrorCounter(MeterRegistry meterRegistry) {
        return Counter.builder("database.error.count")
                .description("Number of database errors")
                .tag("type", "connection")
                .register(meterRegistry);
    }

    @Bean
    public Counter authenticationFailureCounter(MeterRegistry meterRegistry) {
        return Counter.builder("authentication.failure.count")
                .description("Number of authentication failures")
                .register(meterRegistry);
    }
}