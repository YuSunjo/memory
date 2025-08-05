package com.memory.persistence.repository.game;

import com.memory.domain.game.GameQuestion;
import com.memory.domain.game.GameSession;
import com.memory.domain.game.repository.GameQuestionRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.memory.domain.game.QGameQuestion.*;

@Repository
@RequiredArgsConstructor
public class GameQuestionRepositoryCustomImpl implements GameQuestionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Integer findNextQuestionOrder(GameSession gameSession) {
        Integer maxOrder = queryFactory
                .select(gameQuestion.questionOrder.max())
                .from(gameQuestion)
                .where(gameQuestion.gameSession.eq(gameSession))
                .fetchOne();

        return maxOrder != null ? maxOrder + 1 : 1;
    }

    @Override
    public List<GameQuestion> findByGameSessionOrderByQuestionOrder(GameSession gameSession) {
        return queryFactory
                .selectFrom(gameQuestion)
                .where(gameQuestion.gameSession.eq(gameSession))
                .orderBy(gameQuestion.questionOrder.asc())
                .fetch();
    }

    @Override
    public Optional<GameQuestion> findByIdAndGameSession(Long questionId, GameSession gameSession) {
        GameQuestion result = queryFactory
                .selectFrom(gameQuestion)
                .where(
                    gameQuestion.id.eq(questionId),
                    gameQuestion.gameSession.eq(gameSession)
                )
                .fetchOne();
        
        return Optional.ofNullable(result);
    }
}
