package com.memory.domain.game.repository;

import com.memory.domain.game.GameQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameQuestionRepository extends JpaRepository<GameQuestion, Long>, GameQuestionRepositoryCustom {
}
