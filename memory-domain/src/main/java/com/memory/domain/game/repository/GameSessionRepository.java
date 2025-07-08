package com.memory.domain.game.repository;

import com.memory.domain.game.GameSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameSessionRepository extends JpaRepository<GameSession, Long>, GameSessionRepositoryCustom {
}
