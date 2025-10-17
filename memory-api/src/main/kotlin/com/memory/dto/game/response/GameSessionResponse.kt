package com.memory.dto.game.response

import com.memory.domain.game.GameMode
import com.memory.domain.game.GameSession
import com.memory.domain.game.GameSessionStatus
import com.memory.domain.game.GameSetting
import java.time.LocalDateTime

class GameSessionResponse(gameSession: GameSession, gameSetting: GameSetting?) {
    private val id: Long?
    private val memberId: Long?
    private val targetMemberId: Long?
    private val gameMode: GameMode
    private val status: GameSessionStatus
    private val totalScore: Int
    private val totalQuestions: Int
    private val correctAnswers: Int
    private val accuracy: Double
    private val startTime: LocalDateTime
    private val endTime: LocalDateTime?
    private val gameSetting: GameSettingResponse?
    private val createDate: LocalDateTime?

    init {
        this.id = gameSession.id
        this.memberId = gameSession.member.id
        this.targetMemberId = if (gameSession.targetMember != null) gameSession.targetMember!!.id else null
        this.gameMode = gameSession.gameMode
        this.status = gameSession.status
        this.totalScore = gameSession.totalScore
        this.totalQuestions = gameSession.totalQuestions
        this.correctAnswers = gameSession.correctAnswers
        this.accuracy = gameSession.getAccuracy()
        this.startTime = gameSession.startTime
        this.endTime = gameSession.endTime
        this.gameSetting = if (gameSetting != null) GameSettingResponse.Companion.from(gameSetting) else null
        this.createDate = gameSession.createDate
    }

    companion object {
        fun from(gameSession: GameSession, gameSetting: GameSetting?): GameSessionResponse {
            return GameSessionResponse(gameSession, gameSetting)
        }
    }
}
