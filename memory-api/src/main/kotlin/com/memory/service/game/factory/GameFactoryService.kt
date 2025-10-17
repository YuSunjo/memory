package com.memory.service.game.factory

import com.memory.domain.game.GameSession
import com.memory.domain.game.GameSetting
import com.memory.domain.member.Member
import com.memory.dto.game.GameSessionRequest
import com.memory.dto.game.response.GameQuestionResponse

interface GameFactoryService {
    fun createGameSession(member: Member, gameSetting: GameSetting?, request: GameSessionRequest.Create): GameSession?

    fun getNextQuestion(
        member: Member?,
        gameSession: GameSession,
        gameSetting: GameSetting?,
        nextOrder: Int
    ): GameQuestionResponse?
}
