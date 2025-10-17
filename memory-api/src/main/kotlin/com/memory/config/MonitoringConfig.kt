package com.memory.config

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MonitoringConfig {
    @Bean
    fun memoryCreationCounter(meterRegistry: MeterRegistry): Counter {
        return Counter.builder("memory.creation.count")
            .description("Number of memories created")
            .register(meterRegistry)
    }

    @Bean
    fun memberRegistrationCounter(meterRegistry: MeterRegistry): Counter {
        return Counter.builder("member.registration.count")
            .description("Number of members registered")
            .register(meterRegistry)
    }

    @Bean
    fun fileUploadTimer(meterRegistry: MeterRegistry): Timer {
        return Timer.builder("file.upload.duration")
            .description("File upload processing time")
            .register(meterRegistry)
    }

    @Bean
    fun databaseErrorCounter(meterRegistry: MeterRegistry): Counter {
        return Counter.builder("database.error.count")
            .description("Number of database errors")
            .tag("type", "connection")
            .register(meterRegistry)
    }

    @Bean
    fun authenticationFailureCounter(meterRegistry: MeterRegistry): Counter {
        return Counter.builder("authentication.failure.count")
            .description("Number of authentication failures")
            .register(meterRegistry)
    }
}