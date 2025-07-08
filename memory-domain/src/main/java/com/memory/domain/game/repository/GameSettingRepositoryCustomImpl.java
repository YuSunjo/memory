package com.memory.domain.game.repository;

import com.memory.domain.game.GameMode;
import com.memory.domain.game.GameSetting;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.memory.domain.game.QGameSetting.gameSetting;

@RequiredArgsConstructor
public class GameSettingRepositoryCustomImpl implements GameSettingRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<GameSetting> findByGameModeAndIsActiveTrue(GameMode gameMode) {
        return Optional.ofNullable(
            queryFactory
                .selectFrom(gameSetting)
                .where(gameSetting.gameMode.eq(gameMode)
                        .and(gameSetting.isActive.isTrue()))
                .fetchOne()
        );
    }
}
