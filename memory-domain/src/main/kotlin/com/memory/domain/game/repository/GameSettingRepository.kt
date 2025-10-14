package com.memory.domain.game.repository

import com.memory.domain.game.GameSetting
import org.springframework.data.jpa.repository.JpaRepository


interface GameSettingRepository : JpaRepository<GameSetting, Long>, GameSettingRepositoryCustom
