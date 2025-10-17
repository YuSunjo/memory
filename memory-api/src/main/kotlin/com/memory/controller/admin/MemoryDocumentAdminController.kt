package com.memory.controller.admin

import com.memory.annotation.Admin
import com.memory.annotation.swagger.ApiOperations.BasicApi
import com.memory.dto.search.MigrationResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.admin.MemoryDocumentAdminService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.*

/**
 * 서비스와는 상관없이 메모리 문서 관리 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/v1/admin/memory-documents")
@Tag(name = "Memory Document Admin", description = "메모리 문서 관리 API")
class MemoryDocumentAdminController(
    private val memoryDocumentAdminService: MemoryDocumentAdminService,
) {
    @PostMapping("/migrate-all")
    @Admin
    @BasicApi(
        summary = "전체 메모리 마이그레이션",
        description = "데이터베이스의 모든 메모리를 Elasticsearch 인덱스로 마이그레이션합니다.",
        response = MigrationResponse::class
    )
    fun migrateAllMemories(): ServerResponse<MigrationResponse?> {
        val response = memoryDocumentAdminService.migrateAllMemories()
        return success(response)
    }

    @PostMapping("/migrate/{memoryId}")
    @Admin
    @BasicApi(
        summary = "개별 메모리 마이그레이션",
        description = "특정 메모리를 Elasticsearch 인덱스로 마이그레이션합니다.",
        response = MigrationResponse::class
    )
    fun migrateMemory(@PathVariable memoryId: Long): ServerResponse<MigrationResponse?> {
        val response = memoryDocumentAdminService.migrateMemory(memoryId)
        return success(response)
    }

    @DeleteMapping("/delete-all")
    @Admin
    @BasicApi(
        summary = "전체 인덱스 삭제",
        description = "Elasticsearch의 모든 메모리 문서를 삭제합니다.",
        response = MigrationResponse::class
    )
    @Deprecated("")
    fun deleteAllDocuments(): ServerResponse<MigrationResponse?> {
        val response = memoryDocumentAdminService.deleteAllDocuments()
        return success(response)
    }
}