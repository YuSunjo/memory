package com.memory.domain.game.repository

import com.memory.domain.game.GameMode
import com.memory.domain.game.GameSetting
import java.util.Optional

interface GameSettingRepositoryCustom {

    fun findByGameModeAndIsActiveTrue(gameMode: GameMode?): Optional<GameSetting>

}
