package com.memory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = {
    "com.memory",
    "com.memory.persistence.repository",
    "com.memory.search.repository"
})
public class MemoryApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemoryApiApplication.class, args);
    }

}
