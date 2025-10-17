package com.memory.controller.hashtag

import com.memory.annotation.swagger.ApiOperations.BasicApi
import com.memory.domain.hashtag.HashTag
import com.memory.dto.hashtag.response.HashTagResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.hashTag.HashTagService
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "HashTag", description = "해시태그 API")
@RestController
class HashTagController(
    private val hashTagService: HashTagService,
) {
    @BasicApi(summary = "해시태그 검색", description = "키워드가 포함된 해시태그를 검색합니다.", response = HashTagResponse::class)
    @GetMapping("/api/hashtag/search")
    fun searchHashTags(
        @RequestParam keyword: String?,
        @RequestParam(defaultValue = "10") limit: Int
    ): ServerResponse<List<HashTagResponse>?> {
        val response = hashTagService.searchHashTagsByName(keyword, limit).stream()
            .map { obj: HashTag -> HashTagResponse.from(obj) }
            .toList()
        return success(response)
    }

    @BasicApi(summary = "인기 해시태그 조회", description = "사용 횟수가 많은 인기 해시태그를 조회합니다.", response = HashTagResponse::class)
    @GetMapping("/api/hashtag/popular")
    fun getPopularHashTags(
        @RequestParam(defaultValue = "10") limit: Int
    ): ServerResponse<List<HashTagResponse>> {
        val response = hashTagService.getPopularHashTags(limit).stream()
            .map { obj: HashTag -> HashTagResponse.from(obj) }
            .toList()
        return success(response)
    }
}