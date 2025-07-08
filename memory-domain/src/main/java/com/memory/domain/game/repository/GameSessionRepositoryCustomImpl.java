package com.memory.domain.game.repository;

import com.memory.domain.game.GameMode;
import com.memory.domain.game.GameSession;
import com.memory.domain.game.GameSessionStatus;
import com.memory.domain.member.Member;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

import static com.memory.domain.game.QGameSession.gameSession;

@RequiredArgsConstructor
public class GameSessionRepositoryCustomImpl implements GameSessionRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<GameSession> findGameSessionById(Long sessionId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(gameSession)
                .where(gameSession.id.eq(sessionId))
                .fetchOne());
    }

    @Override
    public List<GameSession> findByMemberAndGameMode(Member member, GameMode gameMode) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(gameSession.member.eq(member));

        if (gameMode != null) {
            builder.and(gameSession.gameMode.eq(gameMode));
        }

        return queryFactory
                .selectFrom(gameSession)
                .where(builder)
                .orderBy(gameSession.createDate.desc())
                .fetch();
    }

    @Override
    public Optional<GameSession> findByMemberAndStatus(Member member, GameSessionStatus gameSessionStatus) {
        return Optional.ofNullable(queryFactory
                .selectFrom(gameSession)
                .where(gameSession.member.eq(member)
                        .and(gameSession.status.eq(gameSessionStatus)))
                .fetchOne());
    }
}
