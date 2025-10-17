package com.memory.dto.game.response

import com.memory.domain.game.GameMode
import com.memory.domain.game.GameSetting
import java.time.LocalDateTime

class GameSettingResponse(gameSetting: GameSetting) {
    private val id: Long?
    private val gameMode: GameMode
    private val maxQuestions: Int
    private val timeLimitSeconds: Int
    private val maxDistanceForFullScoreKm: Int
    private val scoringFormula: String
    private val isActive: Boolean
    private val createDate: LocalDateTime?

    init {
        this.id = gameSetting.id
        this.gameMode = gameSetting.gameMode
        this.maxQuestions = gameSetting.maxQuestions
        this.timeLimitSeconds = gameSetting.timeLimitSeconds
        this.maxDistanceForFullScoreKm = gameSetting.maxDistanceForFullScoreKm
        this.scoringFormula = gameSetting.scoringFormula
        this.isActive = gameSetting.isActive
        this.createDate = gameSetting.createDate
    }

    companion object {
        fun from(gameSetting: GameSetting): GameSettingResponse {
            return GameSettingResponse(gameSetting)
        }
    }
}
