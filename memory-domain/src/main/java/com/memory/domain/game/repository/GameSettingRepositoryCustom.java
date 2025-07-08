package com.memory.domain.game.repository;

import com.memory.domain.game.GameMode;
import com.memory.domain.game.GameSetting;

import java.util.Optional;

public interface GameSettingRepositoryCustom {

    Optional<GameSetting> findByGameModeAndIsActiveTrue(GameMode gameMode);

}
