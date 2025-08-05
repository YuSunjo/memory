package com.memory.persistence.repository.game;

import com.memory.domain.game.repository.GameRankingRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class GameRankingRepositoryCustomImpl implements GameRankingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

}
