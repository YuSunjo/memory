package com.memory.controller.memory;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.dto.memory.MemoryRequest;
import com.memory.dto.memory.response.MemoryResponse;
import com.memory.response.ServerResponse;
import com.memory.service.memory.MemoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Memory", description = "Memory API")
public class MemoryController {

    private final MemoryService memoryService;

    @Operation(
        summary = "메모리 생성",
        description = "새로운 메모리를 생성합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "메모리 생성 성공",
            content = @Content(schema = @Schema(implementation = MemoryResponse.class))
        ),
    })
    @Auth
    @PostMapping("api/v1/memories")
    public ServerResponse<MemoryResponse> createMemory(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody @Valid MemoryRequest.Create createRequest) {
        return ServerResponse.success(memoryService.createMemory(memberId, createRequest));
    }

    @Operation(
        summary = "메모리 조회",
        description = "ID로 메모리를 조회합니다."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "메모리 조회 성공",
            content = @Content(schema = @Schema(implementation = MemoryResponse.class))
        ),
    })
    @GetMapping("api/v1/memories/{memoryId}")
    public ServerResponse<MemoryResponse> findMemoryById(
            @PathVariable Long memoryId) {
        return ServerResponse.success(memoryService.findMemoryById(memoryId));
    }

    @Operation(
        summary = "회원별 메모리 조회",
        description = "회원 ID로 메모리를 조회합니다. lastMemoryId와 size 파라미터를 사용하여 페이징 처리가 가능합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "메모리 조회 성공",
            content = @Content(schema = @Schema(implementation = MemoryResponse.class))
        ),
    })
    @Auth
    @GetMapping("api/v1/memories/member")
    public ServerResponse<List<MemoryResponse>> findMemoriesByMember(@Parameter(hidden = true) @MemberId Long memberId, MemoryRequest.GetByMember request) {
        return ServerResponse.success(memoryService.findMemoriesByMember(memberId, request.getLastMemoryId(), request.getSize()));
    }

    @Operation(
        summary = "메모리 수정",
        description = "메모리를 수정합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "메모리 수정 성공",
            content = @Content(schema = @Schema(implementation = MemoryResponse.class))
        ),
    })
    @Auth
    @PutMapping("api/v1/memories/{memoryId}")
    public ServerResponse<MemoryResponse> updateMemory(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long memoryId,
            @RequestBody @Valid MemoryRequest.Update updateRequest) {
        return ServerResponse.success(memoryService.updateMemory(memberId, memoryId, updateRequest));
    }

    @Operation(
        summary = "메모리 삭제",
        description = "메모리를 삭제합니다.",
        security = { @SecurityRequirement(name = "bearerAuth") }
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "204",
            description = "메모리 삭제 성공"
        ),
    })
    @Auth
    @DeleteMapping("api/v1/memories/{memoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMemory(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long memoryId) {
        memoryService.deleteMemory(memberId, memoryId);
    }
}
