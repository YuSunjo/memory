package com.memory.controller.game

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.game.GameQuestionRequest.SubmitAnswer
import com.memory.dto.game.response.GameQuestionResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.game.GameQuestionService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Game Question", description = "게임 문제 관리 API")
class GameQuestionController(
    private val gameQuestionService: GameQuestionService,
) {
    @SecuredApi(
        summary = "다음 문제 조회",
        description = "게임 세션의 다음 문제를 생성하고 조회합니다. 이미지와 함께 문제가 출제됩니다.",
        response = GameQuestionResponse::class
    )
    @Auth
    @PostMapping("api/v1/game/sessions/{sessionId}/next-question")
    fun getNextQuestion(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable sessionId: Long?
    ): ServerResponse<GameQuestionResponse?> {
        return success(gameQuestionService.getNextQuestion(memberId, sessionId))
    }

    @SecuredApi(
        summary = "답안 제출",
        description = "게임 문제에 대한 답안을 제출합니다. 좌표와 소요 시간을 입력하면 점수가 계산됩니다.",
        response = GameQuestionResponse::class
    )
    @Auth
    @PostMapping("api/v1/game/sessions/{sessionId}/questions/{questionId}/answer")
    fun submitAnswer(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable sessionId: Long?,
        @PathVariable questionId: Long?,
        @RequestBody request: @Valid SubmitAnswer
    ): ServerResponse<GameQuestionResponse?> {
        return success(
            gameQuestionService.submitAnswer(
                memberId,
                sessionId,
                questionId,
                request
            )
        )
    }
}
