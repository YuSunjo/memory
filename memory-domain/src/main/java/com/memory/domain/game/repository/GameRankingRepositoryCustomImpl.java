package com.memory.domain.game.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
public class GameRankingRepositoryCustomImpl implements GameRankingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

}
