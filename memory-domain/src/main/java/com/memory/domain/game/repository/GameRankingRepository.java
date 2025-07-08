package com.memory.domain.game.repository;

import com.memory.domain.game.GameRanking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRankingRepository extends JpaRepository<GameRanking, Long>, GameRankingRepositoryCustom {

}
