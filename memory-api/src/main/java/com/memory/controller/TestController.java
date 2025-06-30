package com.memory.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Health Check API")
public class TestController {

    @GetMapping("health")
    public String health() {
        return "OK";
    }
}
