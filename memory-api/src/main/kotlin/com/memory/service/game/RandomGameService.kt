package com.memory.service.game

import com.memory.domain.cities.repository.CitiesRepository
import com.memory.domain.game.GameMode
import com.memory.domain.game.GameQuestion
import com.memory.domain.game.GameSession
import com.memory.domain.game.GameSession.Companion.gameSessionInit
import com.memory.domain.game.GameSetting
import com.memory.domain.game.repository.GameQuestionRepository
import com.memory.domain.member.Member
import com.memory.dto.game.GameSessionRequest
import com.memory.dto.game.response.GameQuestionResponse
import com.memory.exception.customException.NotFoundException
import com.memory.service.game.factory.GameFactoryService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.function.Supplier

@Service
class RandomGameService(
    private val citiesRepository: CitiesRepository,
    private val gameQuestionRepository: GameQuestionRepository,
) : GameFactoryService {
    private val log = LoggerFactory.getLogger(RandomGameService::class.java)

    override fun createGameSession(
        member: Member,
        gameSetting: GameSetting?,
        request: GameSessionRequest.Create
    ): GameSession {
        val gameSession = gameSessionInit(member, GameMode.RANDOM)

        log.info(
            "랜덤 게임 세션 생성 완료 - memberId: {}, sessionId: {}",
            member.id, gameSession.id
        )

        return gameSession
    }

    override fun getNextQuestion(
        member: Member?,
        gameSession: GameSession,
        gameSetting: GameSetting?,
        nextOrder: Int
    ): GameQuestionResponse {
        // 랜덤 위치 생성 (한국 내 좌표)
        val randomLocation = generateRandomWorldLocation()

        val gameQuestion = GameQuestion(
            gameSession,
            null,  // Memory 없음
            nextOrder,
            randomLocation.latitude,
            randomLocation.longitude,
            randomLocation.locationName
        )

        val savedQuestion = gameQuestionRepository.save<GameQuestion>(gameQuestion)

        gameSession.addGameQuestion(savedQuestion)

        return GameQuestionResponse.forQuestion(savedQuestion, null)
    }

    private fun generateRandomWorldLocation(): RandomLocation {
        val cities = citiesRepository.findRandomCities()
            .orElseThrow<NotFoundException?>(Supplier { NotFoundException("도시 데이터가 비어 있습니다.") })

        return RandomLocation(
            BigDecimal.valueOf(cities.latitude).setScale(8, RoundingMode.HALF_UP),
            BigDecimal.valueOf(cities.longitude).setScale(8, RoundingMode.HALF_UP),
            cities.name
        )
    }

    @JvmRecord
    private data class RandomLocation(val latitude: BigDecimal?, val longitude: BigDecimal?, val locationName: String?)
}
