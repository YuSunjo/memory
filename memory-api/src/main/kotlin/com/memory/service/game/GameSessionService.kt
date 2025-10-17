package com.memory.service.game

import com.memory.domain.game.GameMode
import com.memory.domain.game.GameSession
import com.memory.domain.game.GameSessionStatus
import com.memory.domain.game.GameSetting
import com.memory.domain.game.repository.GameSessionRepository
import com.memory.domain.game.repository.GameSettingRepository
import com.memory.domain.member.Member
import com.memory.domain.member.repository.MemberRepository
import com.memory.dto.game.GameSessionRequest
import com.memory.dto.game.GameSessionRequest.GetList
import com.memory.dto.game.response.GameSessionResponse
import com.memory.exception.customException.ConflictException
import com.memory.exception.customException.NotFoundException
import com.memory.exception.customException.ValidationException
import com.memory.service.game.factory.GameFactory
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Consumer

@Service
class GameSessionService(
    private val gameSessionRepository: GameSessionRepository,
    private val gameSettingRepository: GameSettingRepository,
    private val memberRepository: MemberRepository,
    private val gameFactory: GameFactory,
) {
    private val log = LoggerFactory.getLogger(GameSessionService::class.java)

    @Transactional
    fun createGameSession(memberId: Long?, request: GameSessionRequest.Create): GameSessionResponse {
        val member = validateAndGetMember(memberId)
        validateNoInProgressGame(member)
        val gameSetting = validateAndGetGameSetting(request.gameMode)

        // 게임 모드별 로직
        val gameService = gameFactory.getGameService(request.gameMode)
        val gameSession = gameService.createGameSession(member, gameSetting, request)

        val savedSession = gameSessionRepository.save<GameSession>(gameSession as GameSession)

        return GameSessionResponse.from(savedSession, gameSetting)
    }

    @Transactional(readOnly = true)
    fun findGameSessionById(memberId: Long?, sessionId: Long?): GameSessionResponse {
        validateAndGetMember(memberId)

        val gameSession = gameSessionRepository.findGameSessionById(sessionId)
            .orElseThrow { IllegalArgumentException("존재하지 않는 게임 세션입니다.") }

        if (gameSession.member.id != memberId) {
            throw ValidationException("본인의 게임 세션만 조회할 수 있습니다.")
        }

        val gameSetting = gameSettingRepository.findByGameModeAndIsActiveTrue(gameSession.gameMode)
            .orElse(null)

        return GameSessionResponse.from(gameSession, gameSetting)
    }

    @Transactional(readOnly = true)
    fun findProgressGameSession(memberId: Long?): GameSessionResponse {
        val member = validateAndGetMember(memberId)

        val currentSession = gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS)
            .orElseThrow { NotFoundException("현재 진행중인 게임이 없습니다.") }

        val gameSetting = gameSettingRepository.findByGameModeAndIsActiveTrue(currentSession.gameMode)
            .orElse(null)

        return GameSessionResponse.from(currentSession, gameSetting)
    }

    fun findGameSessionsByMember(memberId: Long?, request: GetList): MutableList<GameSessionResponse?> {
        val member = validateAndGetMember(memberId)

        val gameSessions: List<GameSession?> = gameSessionRepository.findByMemberAndGameMode(
            member,
            request.gameMode,
            request.lastSessionId,
            request.size
        )

        return gameSessions.filterNotNull()
            .map { gameSession ->
                val gameSetting = gameSettingRepository.findByGameModeAndIsActiveTrue(gameSession.gameMode)
                    .orElse(null)
                GameSessionResponse.from(gameSession, gameSetting)
            }
            .toMutableList()
    }

    @Transactional
    fun giveUpGameSession(memberId: Long?, sessionId: Long?) {
        memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("존재하지 않는 회원입니다.") }

        val gameSession = gameSessionRepository.findGameSessionById(sessionId)
            .orElseThrow { NotFoundException("존재하지 않는 게임 세션입니다.") }

        if (gameSession.member.id != memberId) {
            throw ValidationException("본인의 게임 세션만 포기할 수 있습니다.")
        }

        if (!gameSession.isInProgress()) {
            throw ValidationException("진행중인 게임만 포기할 수 있습니다.")
        }

        gameSession.giveUpGameSession()
        log.info("게임 세션 포기 완료 - memberId: {}, sessionId: {}", memberId, sessionId)
    }

    private fun validateAndGetMember(memberId: Long?): Member {
        return memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("존재하지 않는 회원입니다.") }
    }

    private fun validateNoInProgressGame(member: Member?) {
        gameSessionRepository.findByMemberAndStatus(member, GameSessionStatus.IN_PROGRESS)
            .ifPresent(Consumer { session: GameSession? ->
                throw ConflictException("이미 진행중인 게임이 있습니다. 먼저 완료하거나 포기해주세요.")
            })
    }

    private fun validateAndGetGameSetting(gameMode: GameMode?): GameSetting {
        return gameSettingRepository.findByGameModeAndIsActiveTrue(gameMode)
            .orElseThrow { NotFoundException("해당 게임 모드의 설정을 찾을 수 없습니다.") }
    }
}
