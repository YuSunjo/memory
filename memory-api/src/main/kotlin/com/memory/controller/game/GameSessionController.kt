package com.memory.controller.game

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.game.GameSessionRequest
import com.memory.dto.game.GameSessionRequest.GetList
import com.memory.dto.game.response.GameSessionResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.game.GameSessionService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Game Session", description = "게임 세션 관리 API")
class GameSessionController(
    private val gameSessionService: GameSessionService,
) {
    @SecuredApi(
        summary = "게임 세션 생성",
        description = "새로운 게임 세션을 시작합니다. 게임 모드에 따라 필요한 파라미터가 다릅니다.",
        response = GameSessionResponse::class
    )
    @Auth
    @PostMapping("/api/v1/game/sessions")
    fun createGameSession(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody request: @Valid GameSessionRequest.Create
    ): ServerResponse<GameSessionResponse?> {
        return success(gameSessionService.createGameSession(memberId, request))
    }

    @SecuredApi(summary = "게임 세션 조회", description = "특정 게임 세션의 상세 정보를 조회합니다.", response = GameSessionResponse::class)
    @Auth
    @GetMapping("/api/v1/game/sessions/{sessionId}")
    fun findGameSessionById(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable sessionId: Long?
    ): ServerResponse<GameSessionResponse?> {
        return success(gameSessionService.findGameSessionById(memberId, sessionId))
    }

    @SecuredApi(
        summary = "현재 진행중인 게임 세션 조회",
        description = "현재 진행중인 게임 세션이 있는지 확인하고 조회합니다.",
        response = GameSessionResponse::class
    )
    @Auth
    @GetMapping("/api/v1/game/sessions/current")
    fun findProgressGameSession(
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<GameSessionResponse?> {
        return success(gameSessionService.findProgressGameSession(memberId))
    }

    @SecuredApi(
        summary = "내 게임 세션 목록 조회",
        description = "본인의 게임 세션 목록을 조회합니다. 게임 모드별로 필터링 가능합니다.",
        response = GameSessionResponse::class
    )
    @Auth
    @GetMapping("/api/v1/game/sessions")
    fun findGameSessionsByMember(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        request: GetList
    ): ServerResponse<MutableList<GameSessionResponse?>?> {
        return success(
            gameSessionService.findGameSessionsByMember(
                memberId,
                request
            )
        )
    }

    @SecuredApi(summary = "게임 세션 포기", description = "진행중인 게임 세션을 포기합니다.")
    @Auth
    @PatchMapping("/api/v1/game/sessions/{sessionId}/give-up")
    fun giveUpGameSession(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable sessionId: Long?
    ): ServerResponse<String> {
        gameSessionService.giveUpGameSession(memberId, sessionId)
        return ServerResponse.OK
    }
}
