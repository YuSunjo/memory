package com.memory

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(
    basePackages = [
        "com.memory",
        "com.memory.persistence.repository",
        "com.memory.search.repository"
    ]
)
class MemoryApiApplicationKT

fun main(args: Array<String>) {
    runApplication<MemoryApiApplicationKT>(*args)
}
