package com.memory.dto.game

import com.memory.domain.game.GameMode
import jakarta.validation.constraints.NotNull

class GameSessionRequest {

    data class Create(
        @field:NotNull(message = "게임 모드는 필수 입력값입니다.")
        val gameMode: GameMode
    )

    data class GetList(
        val gameMode: GameMode? = null,
        val lastSessionId: Long? = null,
        val size: Int = 10
    )
}