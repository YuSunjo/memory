package com.memory.domain.game.repository;

import com.memory.domain.game.GameQuestion;
import com.memory.domain.game.GameSession;

import java.util.List;

public interface GameQuestionRepositoryCustom {

    Integer findNextQuestionOrder(GameSession gameSession);

    List<GameQuestion> findByGameSessionOrderByQuestionOrder(GameSession gameSession);

}
