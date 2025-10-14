package com.memory.domain.game.repository

import com.memory.domain.game.GameMode
import com.memory.domain.game.GameSession
import com.memory.domain.game.GameSessionStatus
import com.memory.domain.member.Member
import java.util.Optional

interface GameSessionRepositoryCustom {

    fun findGameSessionById(sessionId: Long?): Optional<GameSession>

    fun findByMemberAndStatus(member: Member?, gameSessionStatus: GameSessionStatus?): Optional<GameSession>

    fun findByMemberAndGameMode(member: Member?, gameMode: GameMode?, lastSessionId: Long?, size: Int?): List<GameSession>

}
