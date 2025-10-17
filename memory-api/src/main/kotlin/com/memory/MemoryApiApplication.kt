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
class MemoryApiApplication

fun main(args: Array<String>) {
    runApplication<MemoryApiApplication>(*args)
}
