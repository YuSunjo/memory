package com.memory.service.game

import com.memory.domain.file.File
import com.memory.domain.game.GameMode
import com.memory.domain.game.GameQuestion
import com.memory.domain.game.GameQuestion.Companion.init
import com.memory.domain.game.GameSession
import com.memory.domain.game.GameSession.Companion.gameSessionInit
import com.memory.domain.game.GameSetting
import com.memory.domain.game.repository.GameQuestionRepository
import com.memory.domain.member.Member
import com.memory.domain.memory.Memory
import com.memory.domain.memory.repository.MemoryRepository
import com.memory.dto.game.GameSessionRequest
import com.memory.dto.game.response.GameQuestionResponse
import com.memory.exception.customException.NotFoundException
import com.memory.service.game.factory.GameFactoryService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors

@Service
class MyMemoriesGameService(
    private val gameQuestionRepository: GameQuestionRepository,
    private val memoryRepository: MemoryRepository,
) : GameFactoryService {
    private val log = LoggerFactory.getLogger(MyMemoriesGameService::class.java)

    override fun createGameSession(
        member: Member,
        gameSetting: GameSetting?,
        request: GameSessionRequest.Create
    ): GameSession {
        val availableMemories: List<Memory?> = memoryRepository.findMemoriesWithImagesByMember(member)
        check(availableMemories.size >= MIN_MEMORIES_FOR_GAME) {
            String.format(
                "내 추억 게임을 시작하기 위해서는 최소 %d개의 이미지가 있는 추억이 필요합니다.",
                MIN_MEMORIES_FOR_GAME
            )
        }

        val gameSession = gameSessionInit(member, GameMode.MY_MEMORIES)

        log.info(
            "내 추억 게임 세션 생성 완료 - memberId: {}, sessionId: {}",
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
        // 이미 사용된 Memory ID들 조회
        val existingQuestions: List<GameQuestion?> =
            gameQuestionRepository.findByGameSessionOrderByQuestionOrder(gameSession)
        val usedMemoryIds = existingQuestions.stream()
            .map<Long?> { q: GameQuestion? -> q!!.memory!!.id }
            .collect(Collectors.toList())

        val selectedMemory = selectRandomMemory(gameSession, usedMemoryIds)

        // GameQuestion 생성
        val latitude = selectedMemory.map!!.latitude
        val longitude = selectedMemory.map!!.longitude

        val gameQuestion = init(
            gameSession,
            selectedMemory,
            nextOrder,
            BigDecimal(latitude),
            BigDecimal(longitude),
            selectedMemory.locationName
        )

        val savedQuestion = gameQuestionRepository.save<GameQuestion>(gameQuestion)

        gameSession.addGameQuestion(savedQuestion)

        val imageUrls = selectedMemory.files.stream()
            .map<String?>(File::fileUrl)
            .toList()

        return GameQuestionResponse.forQuestion(savedQuestion, imageUrls)
    }

    private fun selectRandomMemory(gameSession: GameSession, usedMemoryIds: MutableList<Long?>): Memory {
        val availableMemories: List<Memory?> =
            memoryRepository.findMemoriesWithImagesByMember(gameSession.member)

        // 이미 사용된 Memory 제외
        val unusedMemories: MutableList<Memory> = availableMemories.stream()
            .filter { memory: Memory? -> !usedMemoryIds.contains(memory!!.id) }
            .collect(Collectors.toList())

        if (unusedMemories.isEmpty()) {
            throw NotFoundException("사용 가능한 내 추억이 부족합니다.")
        }

        Collections.shuffle(unusedMemories)
        return unusedMemories.get(0)
    }

    companion object {
        private const val MIN_MEMORIES_FOR_GAME = 3
    }
}
