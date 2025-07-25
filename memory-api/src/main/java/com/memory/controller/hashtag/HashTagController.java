package com.memory.controller.hashtag;

import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.hashtag.response.HashTagResponse;
import com.memory.response.ServerResponse;
import com.memory.service.hashTag.HashTagService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "HashTag", description = "해시태그 API")
@RestController
@RequiredArgsConstructor
public class HashTagController {

    private final HashTagService hashTagService;

    @ApiOperations.BasicApi(
            summary = "해시태그 검색",
            description = "키워드가 포함된 해시태그를 검색합니다.",
            response = HashTagResponse.class
    )
    @GetMapping("/api/hashtag/search")
    public ServerResponse<List<HashTagResponse>> searchHashTags(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<HashTagResponse> response = hashTagService.searchHashTagsByName(keyword, limit).stream()
                .map(HashTagResponse::from)
                .toList();
        return ServerResponse.success(response);
    }

    @ApiOperations.BasicApi(
            summary = "인기 해시태그 조회",
            description = "사용 횟수가 많은 인기 해시태그를 조회합니다.",
            response = HashTagResponse.class
    )
    @GetMapping("/api/hashtag/popular")
    public ServerResponse<List<HashTagResponse>> getPopularHashTags(
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<HashTagResponse> response = hashTagService.getPopularHashTags(limit).stream()
                .map(HashTagResponse::from)
                .toList();
        return ServerResponse.success(response);
    }
}