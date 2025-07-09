package com.memory.domain.game.repository;

import com.memory.domain.game.GameQuestion;
import com.memory.domain.game.GameSession;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.memory.domain.game.QGameQuestion.*;

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
}
