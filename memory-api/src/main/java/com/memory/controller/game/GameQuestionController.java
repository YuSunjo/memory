package com.memory.controller.game;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.game.response.GameQuestionResponse;
import com.memory.response.ServerResponse;
import com.memory.service.game.GameQuestionService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Game Question", description = "게임 문제 관리 API")
public class GameQuestionController {

    private final GameQuestionService gameQuestionService;

    @ApiOperations.SecuredApi(
        summary = "다음 문제 조회",
        description = "게임 세션의 다음 문제를 생성하고 조회합니다. 이미지와 함께 문제가 출제됩니다.",
        response = GameQuestionResponse.class
    )
    @Auth
    @GetMapping("api/v1/game/sessions/{sessionId}/next-question")
    public ServerResponse<GameQuestionResponse> getNextQuestion(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long sessionId) {
        return ServerResponse.success(gameQuestionService.getNextQuestion(memberId, sessionId));
    }
}
