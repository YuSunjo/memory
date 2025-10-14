package com.memory.domain.game.repository

import com.memory.domain.game.GameRanking
import org.springframework.data.jpa.repository.JpaRepository

interface GameRankingRepository : JpaRepository<GameRanking, Long>, GameRankingRepositoryCustom
