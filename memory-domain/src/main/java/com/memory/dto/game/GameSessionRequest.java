package com.memory.dto.game;

import com.memory.domain.game.GameMode;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class GameSessionRequest {

    @Getter
    public static class Create {
        @NotNull(message = "게임 모드는 필수 입력값입니다.")
        private GameMode gameMode;

        public Create(GameMode gameMode) {
            this.gameMode = gameMode;
        }
    }

    @Getter
    public static class GetList {
        private final GameMode gameMode;
        private final Long lastSessionId;
        private final Integer size;

        public GetList(GameMode gameMode, Long lastSessionId, Integer size) {
            this.gameMode = gameMode;
            this.lastSessionId = lastSessionId;
            this.size = size != null ? size : 10;
        }
    }
}
