package com.memory.controller.memory;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.memory.MemoryRequest;
import com.memory.dto.memory.response.MemoryResponse;
import com.memory.response.ServerResponse;
import com.memory.service.memory.MemoryService;
import com.memory.useCase.memory.MemoryUseCase;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Memory", description = "Memory API")
public class MemoryController {

    private final MemoryUseCase memoryUseCase;
    private final MemoryService memoryService;

    @ApiOperations.SecuredApi(
        summary = "메모리 생성",
        description = "새로운 메모리를 생성합니다.",
        response = MemoryResponse.class
    )
    @Auth
    @PostMapping("api/v1/memories")
    public ServerResponse<MemoryResponse> createMemory(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody @Valid MemoryRequest.Create createRequest) {
        return ServerResponse.success(memoryUseCase.createMemoryWithHashTags(memberId, createRequest));
    }

    @ApiOperations.SecuredApi(
        summary = "메모리 조회",
        description = "메모리 ID로 메모리를 조회합니다.",
        response = MemoryResponse.class
    )
    @Auth
    @GetMapping("api/v1/memories/{memoryId}")
    public ServerResponse<MemoryResponse> findMemoryById(
            @PathVariable Long memoryId, @MemberId Long memberId) {
        return ServerResponse.success(memoryService.findMemoryById(memberId, memoryId));
    }

    @ApiOperations.SecuredApi(
            summary = "퍼블릭 메모리 조회",
            description = "메모리 ID로 메모리를 조회합니다.",
            response = MemoryResponse.class
    )
    @GetMapping("api/v1/memories/public/{memoryId}")
    public ServerResponse<MemoryResponse> findPublicMemoryById(
            @PathVariable Long memoryId) {
        return ServerResponse.success(memoryService.findPublicMemoryById(memoryId));
    }

    @ApiOperations.SecuredApi(
        summary = "회원의 메모리 목록 조회",
        description = "회원의 메모리 목록을 조회합니다. lastMemoryId를 통해 페이징 처리할 수 있습니다. memoryType을 통해 메모리 타입별로 조회할 수 있습니다.",
        response = MemoryResponse.class
    )
    @Auth
    @GetMapping("api/v1/memories/member")
    public ServerResponse<List<MemoryResponse>> findMemoriesByMember(@Parameter(hidden = true) @MemberId Long memberId, MemoryRequest.GetByMember request) {
        return ServerResponse.success(memoryService.findMemoriesByMember(memberId, request.getLastMemoryId(), request.getSize(), request.getMemoryType()));
    }

    @ApiOperations.SecuredApi(
        summary = "메모리 수정",
        description = "기존 메모리를 수정합니다.",
        response = MemoryResponse.class
    )
    @Auth
    @PutMapping("api/v1/memories/{memoryId}")
    public ServerResponse<MemoryResponse> updateMemory(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long memoryId,
            @RequestBody @Valid MemoryRequest.Update updateRequest) {
        return ServerResponse.success(memoryUseCase.updateMemoryWithHashTags(memberId, memoryId, updateRequest));
    }

    @ApiOperations.SecuredApi(
        summary = "메모리 삭제",
        description = "기존 메모리를 삭제합니다."
    )
    @Auth
    @DeleteMapping("api/v1/memories/{memoryId}")
    public ServerResponse<String> deleteMemory(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long memoryId) {
        memoryService.deleteMemory(memberId, memoryId);
        return ServerResponse.OK;
    }

    @ApiOperations.BasicApi(
        summary = "공개 메모리 목록 조회",
        description = "모든 공개(PUBLIC) 메모리 목록을 조회합니다. 로그인 없이 접근 가능합니다. lastMemoryId를 통해 페이징 처리할 수 있습니다.",
        response = MemoryResponse.class
    )
    @GetMapping("api/v1/memories/public")
    public ServerResponse<List<MemoryResponse>> findPublicMemories(MemoryRequest.GetByPublic request) {
        return ServerResponse.success(memoryService.findPublicMemories(request.getLastMemoryId(), request.getSize()));
    }
}
