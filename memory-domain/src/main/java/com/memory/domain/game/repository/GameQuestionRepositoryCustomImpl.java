package com.memory.domain.game.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GameQuestionRepositoryCustomImpl {

    private final JPAQueryFactory queryFactory;
}
