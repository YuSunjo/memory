package com.memory.persistence.repository.game;

import com.memory.domain.game.GameMode;
import com.memory.domain.game.GameSetting;
import com.memory.domain.game.repository.GameSettingRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.memory.domain.game.QGameSetting.gameSetting;

@Repository
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
