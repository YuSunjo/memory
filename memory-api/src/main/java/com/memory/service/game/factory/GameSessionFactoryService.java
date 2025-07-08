package com.memory.service.game.factory;

import com.memory.domain.game.GameSession;
import com.memory.dto.game.GameSessionRequest;

public interface GameSessionFactoryService {

    GameSession createGameSession(Long memberId, GameSessionRequest.Create request);
}
