package com.memory.domain.game.repository

import com.memory.domain.game.GameQuestion
import org.springframework.data.jpa.repository.JpaRepository

interface GameQuestionRepository : JpaRepository<GameQuestion, Long>, GameQuestionRepositoryCustom
