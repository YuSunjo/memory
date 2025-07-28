package com.memory.controller.search;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.search.MemorySearchRequest;
import com.memory.dto.search.SearchResultResponse;
import com.memory.response.ServerResponse;
import com.memory.service.search.MemorySearchService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}