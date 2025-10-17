package com.memory.controller.diary

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.diary.DiaryRequest
import com.memory.dto.diary.DiaryRequest.GetByDateRange
import com.memory.dto.diary.response.DiaryResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.diary.DiaryService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Diary", description = "Diary API")
class DiaryController(
    private val diaryService: DiaryService,
) {
    @SecuredApi(summary = "다이어리 생성", description = "새로운 다이어리를 생성합니다.", response = DiaryResponse::class)
    @Auth
    @PostMapping("api/v1/diaries")
    fun createDiary(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody request: @Valid DiaryRequest.Create
    ): ServerResponse<DiaryResponse?> {
        return success(diaryService.createDiary(memberId, request))
    }

    @SecuredApi(summary = "다이어리 수정", description = "기존 다이어리를 수정합니다.", response = DiaryResponse::class)
    @Auth
    @PutMapping("api/v1/diaries/{diaryId}")
    fun updateDiary(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable diaryId: Long,
        @RequestBody request: @Valid DiaryRequest.Update
    ): ServerResponse<DiaryResponse?> {
        return success(diaryService.updateDiary(memberId, diaryId, request))
    }

    @SecuredApi(summary = "다이어리 삭제", description = "다이어리를 삭제합니다.")
    @Auth
    @DeleteMapping("api/v1/diaries/{diaryId}")
    fun deleteDiary(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable diaryId: Long
    ): ServerResponse<String> {
        diaryService.deleteDiary(memberId, diaryId)
        return ServerResponse.OK
    }

    @SecuredApi(summary = "기간별 다이어리 조회", description = "특정 기간 내의 다이어리를 조회합니다.", response = DiaryResponse::class)
    @Auth
    @GetMapping("api/v1/diaries/date-range")
    fun getDiariesByDateRange(
        @Parameter(hidden = true) @MemberId memberId: Long?, request: GetByDateRange
    ): ServerResponse<MutableList<DiaryResponse?>?> {
        return success(
            diaryService.getDiariesByDateRange(
                memberId,
                request.startDate,
                request.endDate
            )
        )
    }
}