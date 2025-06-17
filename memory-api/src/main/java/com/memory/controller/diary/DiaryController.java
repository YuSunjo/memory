package com.memory.controller.diary;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.diary.DiaryRequest;
import com.memory.dto.diary.response.DiaryResponse;
import com.memory.response.ServerResponse;
import com.memory.service.diary.DiaryService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Diary", description = "Diary API")
public class DiaryController {

    private final DiaryService diaryService;

    @ApiOperations.SecuredApi(
        summary = "다이어리 생성",
        description = "새로운 다이어리를 생성합니다.",
        response = DiaryResponse.class
    )
    @Auth
    @PostMapping("api/v1/diaries")
    public ServerResponse<DiaryResponse> createDiary(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody @Valid DiaryRequest.Create request) {
        return ServerResponse.success(diaryService.createDiary(memberId, request));
    }

    @ApiOperations.SecuredApi(
        summary = "다이어리 수정",
        description = "기존 다이어리를 수정합니다.",
        response = DiaryResponse.class
    )
    @Auth
    @PutMapping("api/v1/diaries/{diaryId}")
    public ServerResponse<DiaryResponse> updateDiary(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long diaryId,
            @RequestBody @Valid DiaryRequest.Update request) {
        return ServerResponse.success(diaryService.updateDiary(memberId, diaryId, request));
    }

    @ApiOperations.SecuredApi(
        summary = "다이어리 삭제",
        description = "다이어리를 삭제합니다."
    )
    @Auth
    @DeleteMapping("api/v1/diaries/{diaryId}")
    public ServerResponse<String> deleteDiary(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long diaryId) {
        diaryService.deleteDiary(memberId, diaryId);
        return ServerResponse.OK;
    }

    @ApiOperations.SecuredApi(
        summary = "기간별 다이어리 조회",
        description = "특정 기간 내의 다이어리를 조회합니다.",
        response = DiaryResponse.class
    )
    @Auth
    @GetMapping("api/v1/diaries/date-range")
    public ServerResponse<List<DiaryResponse>> getDiariesByDateRange(
            @Parameter(hidden = true) @MemberId Long memberId, DiaryRequest.GetByDateRange request) {
        return ServerResponse.success(diaryService.getDiariesByDateRange(memberId, request.getStartDate(), request.getEndDate()));
    }
}