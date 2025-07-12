package com.memory.service.game.factory;

import com.memory.domain.game.GameSession;
import com.memory.domain.game.GameSetting;
import com.memory.domain.member.Member;
import com.memory.dto.game.GameSessionRequest;
import com.memory.dto.game.response.GameQuestionResponse;

public interface GameFactoryService {

    GameSession createGameSession(Member member, GameSetting gameSetting, GameSessionRequest.Create request);

    GameQuestionResponse getNextQuestion(Member member, GameSession gameSession, GameSetting gameSetting, Integer nextOrder);
}
