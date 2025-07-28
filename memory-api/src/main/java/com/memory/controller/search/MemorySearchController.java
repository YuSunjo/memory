package com.memory.controller.search;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.search.*;
import com.memory.response.ServerResponse;
import com.memory.service.search.MemorySearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/memories")
@RequiredArgsConstructor
@Tag(name = "Memory Search", description = "메모리 검색 API")
public class MemorySearchController {

    private final MemorySearchService memorySearchService;

    @PostMapping("/search")
    @Auth
    @ApiOperations.SecuredApi(
        summary = "내 메모리 검색",
        description = "인증된 사용자의 메모리를 검색합니다. 본인의 모든 메모리(PRIVATE 포함) + 관계된 사용자의 메모리 + 다른 사용자의 PUBLIC 메모리를 검색합니다.",
        response = SearchResultResponse.class
    )
    public ServerResponse<SearchResultResponse> searchMyMemories(
            @Valid @RequestBody MemorySearchRequest request,
            @MemberId Long memberId) {
        
        SearchResultResponse response = memorySearchService.searchAuthenticated(request, memberId);
        return ServerResponse.success(response);
    }

    @PostMapping("/public/search")
    @ApiOperations.BasicApi(
        summary = "공개 메모리 검색",
        description = "로그인 없이 PUBLIC 메모리만 검색합니다. 하이라이팅 기능을 지원합니다.",
        response = SearchResultResponse.class
    )
    public ServerResponse<SearchResultResponse> searchPublicMemories(@Valid @RequestBody MemorySearchRequest request) {
        
        SearchResultResponse response = memorySearchService.searchPublic(request);
        return ServerResponse.success(response);
    }

    // ===== 자동완성 API =====

    @GetMapping("/autocomplete")
    @Auth
    @ApiOperations.SecuredApi(
        summary = "메모리 자동완성",
        description = "인증된 사용자의 자동완성을 제공합니다. 본인 + 관계된 사용자 + PUBLIC 메모리에서 제목과 해시태그를 검색합니다.",
        response = AutocompleteResponse.class
    )
    public ServerResponse<AutocompleteResponse> getAutocomplete(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit,
            @MemberId Long memberId) {
        
        AutocompleteResponse response = memorySearchService.getAuthenticatedAutocomplete(memberId, query, limit);
        return ServerResponse.success(response);
    }

    @GetMapping("/public/autocomplete")
    @ApiOperations.BasicApi(
        summary = "공개 메모리 자동완성",
        description = "로그인 없이 PUBLIC 메모리에서 자동완성을 제공합니다. 제목과 해시태그를 검색합니다.",
        response = AutocompleteResponse.class
    )
    public ServerResponse<AutocompleteResponse> getPublicAutocomplete(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int limit) {
        
        AutocompleteResponse response = memorySearchService.getPublicAutocomplete(query, limit);
        return ServerResponse.success(response);
    }
}