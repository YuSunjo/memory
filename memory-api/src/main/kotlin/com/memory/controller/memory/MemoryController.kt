package com.memory.controller.memory

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.BasicApi
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.memory.MemoryRequest
import com.memory.dto.memory.MemoryRequest.GetByMember
import com.memory.dto.memory.MemoryRequest.GetByPublic
import com.memory.dto.memory.response.MemoryResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.memory.MemoryService
import com.memory.useCase.memory.MemoryUseCase
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Memory", description = "Memory API")
class MemoryController(
    private val memoryUseCase: MemoryUseCase,
    private val memoryService: MemoryService,
) {
    @SecuredApi(summary = "메모리 생성", description = "새로운 메모리를 생성합니다.", response = MemoryResponse::class)
    @Auth
    @PostMapping("api/v1/memories")
    fun createMemory(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody createRequest: @Valid MemoryRequest.Create
    ): ServerResponse<MemoryResponse?> {
        return success(memoryUseCase.createMemoryWithHashTags(memberId, createRequest))
    }

    @SecuredApi(summary = "메모리 조회", description = "메모리 ID로 메모리를 조회합니다.", response = MemoryResponse::class)
    @Auth
    @GetMapping("api/v1/memories/{memoryId}")
    fun findMemoryById(
        @PathVariable memoryId: Long?, @MemberId memberId: Long?
    ): ServerResponse<MemoryResponse?> {
        return success(memoryService.findMemoryById(memberId, memoryId))
    }

    @SecuredApi(summary = "퍼블릭 메모리 조회", description = "메모리 ID로 메모리를 조회합니다.", response = MemoryResponse::class)
    @GetMapping("api/v1/memories/public/{memoryId}")
    fun findPublicMemoryById(
        @PathVariable memoryId: Long
    ): ServerResponse<MemoryResponse?> {
        return success(memoryService.findPublicMemoryById(memoryId))
    }

    @SecuredApi(
        summary = "회원의 메모리 목록 조회",
        description = "회원의 메모리 목록을 조회합니다. lastMemoryId를 통해 페이징 처리할 수 있습니다. memoryType을 통해 메모리 타입별로 조회할 수 있습니다.",
        response = MemoryResponse::class
    )
    @Auth
    @GetMapping("api/v1/memories/member")
    fun findMemoriesByMember(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        request: GetByMember
    ): ServerResponse<MutableList<MemoryResponse?>?> {
        return success(
            memoryService.findMemoriesByMember(
                memberId,
                request.lastMemoryId,
                request.size,
                request.memoryType
            )
        )
    }

    @SecuredApi(summary = "메모리 수정", description = "기존 메모리를 수정합니다.", response = MemoryResponse::class)
    @Auth
    @PutMapping("api/v1/memories/{memoryId}")
    fun updateMemory(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable memoryId: Long?,
        @RequestBody updateRequest: @Valid MemoryRequest.Update
    ): ServerResponse<MemoryResponse?> {
        return success(memoryUseCase.updateMemoryWithHashTags(memberId, memoryId, updateRequest))
    }

    @SecuredApi(summary = "메모리 삭제", description = "기존 메모리를 삭제합니다.")
    @Auth
    @DeleteMapping("api/v1/memories/{memoryId}")
    fun deleteMemory(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable memoryId: Long
    ): ServerResponse<String> {
        memoryUseCase.deleteMemory(memberId, memoryId)
        return ServerResponse.OK
    }

    @BasicApi(
        summary = "공개 메모리 목록 조회",
        description = "모든 공개(PUBLIC) 메모리 목록을 조회합니다. 로그인 없이 접근 가능합니다. lastMemoryId를 통해 페이징 처리할 수 있습니다.",
        response = MemoryResponse::class
    )
    @GetMapping("api/v1/memories/public")
    fun findPublicMemories(request: GetByPublic): ServerResponse<MutableList<MemoryResponse?>?> {
        return success(
            memoryService.findPublicMemories(
                request.lastMemoryId,
                request.size
            )
        )
    }
}
