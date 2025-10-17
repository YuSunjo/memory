package com.memory.controller

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Health", description = "Health Check API")
class TestController {
    @GetMapping("health")
    fun health(): String {
        return "OK"
    }
}