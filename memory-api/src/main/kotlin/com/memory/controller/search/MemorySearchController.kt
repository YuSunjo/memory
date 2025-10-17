package com.memory.controller.search

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.BasicApi
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.search.AutocompleteResponse
import com.memory.dto.search.MemorySearchRequest
import com.memory.dto.search.SearchResultResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.search.MemorySearchService
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/memories")
@Tag(name = "Memory Search", description = "메모리 검색 API")
class MemorySearchController(
    private val memorySearchService: MemorySearchService,
) {
    @PostMapping("/search")
    @Auth
    @SecuredApi(
        summary = "내 메모리 검색",
        description = "인증된 사용자의 메모리를 검색합니다. 본인의 모든 메모리(PRIVATE 포함) + 관계된 사용자의 메모리 + 다른 사용자의 PUBLIC 메모리를 검색합니다.",
        response = SearchResultResponse::class
    )
    fun searchMyMemories(
        @RequestBody request: @Valid MemorySearchRequest?,
        @MemberId memberId: Long?
    ): ServerResponse<SearchResultResponse?> {
        val response = memorySearchService.searchAuthenticated(request, memberId)
        return success(response)
    }

    @PostMapping("/public/search")
    @BasicApi(
        summary = "공개 메모리 검색",
        description = "로그인 없이 PUBLIC 메모리만 검색합니다. 하이라이팅 기능을 지원합니다.",
        response = SearchResultResponse::class
    )
    fun searchPublicMemories(@RequestBody request: @Valid MemorySearchRequest?): ServerResponse<SearchResultResponse?> {
        val response = memorySearchService.searchPublic(request)
        return success(response)
    }

    // ===== 자동완성 API =====
    @GetMapping("/autocomplete")
    @Auth
    @SecuredApi(
        summary = "메모리 자동완성",
        description = "인증된 사용자의 자동완성을 제공합니다. 본인 + 관계된 사용자 + PUBLIC 메모리에서 제목과 해시태그를 검색합니다.",
        response = AutocompleteResponse::class
    )
    fun getAutocomplete(
        @RequestParam query: String?,
        @RequestParam(defaultValue = "10") limit: Int,
        @MemberId memberId: Long?
    ): ServerResponse<AutocompleteResponse?> {
        val response = memorySearchService.getAuthenticatedAutocomplete(memberId, query, limit)
        return success(response)
    }

    @GetMapping("/public/autocomplete")
    @BasicApi(
        summary = "공개 메모리 자동완성",
        description = "로그인 없이 PUBLIC 메모리에서 자동완성을 제공합니다. 제목과 해시태그를 검색합니다.",
        response = AutocompleteResponse::class
    )
    fun getPublicAutocomplete(
        @RequestParam query: String?,
        @RequestParam(defaultValue = "10") limit: Int
    ): ServerResponse<AutocompleteResponse?> {
        val response = memorySearchService.getPublicAutocomplete(query, limit)
        return success(response)
    }
}