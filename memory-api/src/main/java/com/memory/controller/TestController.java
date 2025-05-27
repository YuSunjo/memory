package com.memory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Health", description = "Health Check API")
public class TestController {

    @Operation(
        summary = "Health Check",
        description = "서버의 상태를 확인합니다.",
        tags = {"Health"}
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "서버가 정상적으로 동작 중입니다.",
            content = @Content(schema = @Schema(implementation = String.class))
        )
    })
    @GetMapping("health")
    public String health() {
        return "OK";
    }
}
