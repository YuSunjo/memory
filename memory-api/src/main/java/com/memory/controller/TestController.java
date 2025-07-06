package com.memory.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Health Check API")
@RequiredArgsConstructor
public class TestController {

    private final JdbcTemplate jdbcTemplate;

    @GetMapping("health")
    public String health() {
        try {
            String currentTime = jdbcTemplate.queryForObject("SELECT NOW()::text", String.class);
            return "OK - DB Connected at: " + currentTime;
        } catch (Exception e) {
            return "ERROR - DB Connection Failed: " + e.getMessage();
        }
    }
}
