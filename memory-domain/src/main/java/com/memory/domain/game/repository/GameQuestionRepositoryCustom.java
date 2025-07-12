package com.memory.domain.game.repository;

import com.memory.domain.game.GameQuestion;
import com.memory.domain.game.GameSession;

import java.util.List;
import java.util.Optional;

public interface GameQuestionRepositoryCustom {

    Integer findNextQuestionOrder(GameSession gameSession);

    List<GameQuestion> findByGameSessionOrderByQuestionOrder(GameSession gameSession);

    Optional<GameQuestion> findByIdAndGameSession(Long questionId, GameSession gameSession);

}
