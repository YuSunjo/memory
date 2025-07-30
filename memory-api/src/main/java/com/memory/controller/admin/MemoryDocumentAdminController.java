package com.memory.controller.admin;

import com.memory.annotation.Admin;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.search.MigrationResponse;
import com.memory.response.ServerResponse;
import com.memory.service.admin.MemoryDocumentAdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 서비스와는 상관없이 메모리 문서 관리 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/v1/admin/memory-documents")
@RequiredArgsConstructor
@Tag(name = "Memory Document Admin", description = "메모리 문서 관리 API")
public class MemoryDocumentAdminController {

    private final MemoryDocumentAdminService memoryDocumentAdminService;

    @PostMapping("/migrate-all")
    @Admin
    @ApiOperations.BasicApi(
        summary = "전체 메모리 마이그레이션",
        description = "데이터베이스의 모든 메모리를 Elasticsearch 인덱스로 마이그레이션합니다.",
        response = MigrationResponse.class
    )
    public ServerResponse<MigrationResponse> migrateAllMemories() {
        MigrationResponse response = memoryDocumentAdminService.migrateAllMemories();
        return ServerResponse.success(response);
    }

    @PostMapping("/migrate/{memoryId}")
    @Admin
    @ApiOperations.BasicApi(
        summary = "개별 메모리 마이그레이션",
        description = "특정 메모리를 Elasticsearch 인덱스로 마이그레이션합니다.",
        response = MigrationResponse.class
    )
    public ServerResponse<MigrationResponse> migrateMemory(@PathVariable Long memoryId) {
        MigrationResponse response = memoryDocumentAdminService.migrateMemory(memoryId);
        return ServerResponse.success(response);
    }

    @DeleteMapping("/delete-all")
    @Admin
    @ApiOperations.BasicApi(
        summary = "전체 인덱스 삭제",
        description = "Elasticsearch의 모든 메모리 문서를 삭제합니다.",
        response = MigrationResponse.class
    )
    @Deprecated
    public ServerResponse<MigrationResponse> deleteAllDocuments() {
        MigrationResponse response = memoryDocumentAdminService.deleteAllDocuments();
        return ServerResponse.success(response);
    }
}