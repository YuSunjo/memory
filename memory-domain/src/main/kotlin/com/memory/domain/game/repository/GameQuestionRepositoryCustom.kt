package com.memory.domain.game.repository

import com.memory.domain.game.GameQuestion
import com.memory.domain.game.GameSession
import java.util.Optional

interface GameQuestionRepositoryCustom {

    fun findNextQuestionOrder(gameSession: GameSession?): Int?

    fun findByGameSessionOrderByQuestionOrder(gameSession: GameSession?): List<GameQuestion>

    fun findByIdAndGameSession(questionId: Long?, gameSession: GameSession?): Optional<GameQuestion>

}
