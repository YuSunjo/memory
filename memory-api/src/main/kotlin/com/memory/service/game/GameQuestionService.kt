package com.memory.service.game

import com.memory.domain.game.GameSession
import com.memory.domain.game.GameSetting
import com.memory.domain.game.repository.GameQuestionRepository
import com.memory.domain.game.repository.GameSessionRepository
import com.memory.domain.game.repository.GameSettingRepository
import com.memory.domain.member.repository.MemberRepository
import com.memory.dto.game.GameQuestionRequest.SubmitAnswer
import com.memory.dto.game.response.GameQuestionResponse
import com.memory.exception.customException.ConflictException
import com.memory.exception.customException.NotFoundException
import com.memory.exception.customException.ValidationException
import com.memory.service.game.factory.GameFactory
import com.memory.util.GeographyUtils.calculateDistance
import com.memory.util.GeographyUtils.calculateScore
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class GameQuestionService(
    private val gameSessionRepository: GameSessionRepository,
    private val gameSettingRepository: GameSettingRepository,
    private val gameQuestionRepository: GameQuestionRepository,
    private val memberRepository: MemberRepository,
    private val gameFactory: GameFactory,
) {
    private val log = LoggerFactory.getLogger(GameQuestionService::class.java)

    @Transactional
    fun getNextQuestion(memberId: Long?, sessionId: Long?): GameQuestionResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("존재하지 않는 회원입니다.") }

        val gameSession = gameSessionRepository.findGameSessionById(sessionId)
            .orElseThrow { NotFoundException("존재하지 않는 게임 세션입니다.") }

        if (!gameSession.isOwner(member)) {
            throw ConflictException("본인의 게임 세션만 조회할 수 있습니다.")
        }
        if (!gameSession.isInProgress()) {
            throw ValidationException("진행 중인 게임 세션이 아닙니다.")
        }

        val gameSetting = gameSettingRepository.findByGameModeAndIsActiveTrue(gameSession.gameMode)
            .orElseThrow { NotFoundException("해당 게임 모드의 설정을 찾을 수 없습니다.") }

        val nextOrder = validateQuestionGeneration(gameSession, gameSetting)

        // 게임 모드에 따라 적절한 GameFactoryService
        val gameService = gameFactory.getGameService(gameSession.gameMode)
        val nextQuestion = gameService.getNextQuestion(member, gameSession, gameSetting, nextOrder)

        log.info(
            "다음 문제 생성 완료 - memberId: {}, sessionId: {}, nextOrder: {}",
            member.id, gameSession.id, nextOrder
        )
        return nextQuestion
    }

    @Transactional
    fun submitAnswer(
        memberId: Long?, sessionId: Long?, questionId: Long?,
        request: SubmitAnswer
    ): GameQuestionResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("존재하지 않는 회원입니다.") }

        val gameSession = gameSessionRepository.findGameSessionById(sessionId)
            .orElseThrow { NotFoundException("존재하지 않는 게임 세션입니다.") }

        if (!gameSession.isOwner(member)) {
            throw ConflictException("본인의 게임 세션만 답안 제출이 가능합니다.")
        }
        if (!gameSession.isInProgress()) {
            throw ValidationException("진행 중인 게임 세션이 아닙니다.")
        }

        val gameQuestion = gameQuestionRepository.findByIdAndGameSession(questionId, gameSession)
            .orElseThrow { NotFoundException("해당 게임 세션에서 문제를 찾을 수 없습니다.") }

        if (gameQuestion.isAnswered()) {
            throw ConflictException("이미 답안이 제출된 문제입니다.")
        }

        val gameSetting = gameSettingRepository.findByGameModeAndIsActiveTrue(gameSession.gameMode)
            .orElseThrow { NotFoundException("해당 게임 모드의 설정을 찾을 수 없습니다.") }

        // 거리 계산
        val distanceKm = calculateDistance(
            gameQuestion.correctLatitude,
            gameQuestion.correctLongitude,
            request.getPlayerLatitude(),
            request.getPlayerLongitude()
        )

        // 점수 계산
        val score = calculateScore(distanceKm, gameSetting.maxDistanceForFullScoreKm)

        // 답안 제출
        gameQuestion.submitAnswer(
            request.getPlayerLatitude(),
            request.getPlayerLongitude(),
            distanceKm,
            score,
            request.getTimeTakenSeconds()
        )

        // 게임 세션 점수 업데이트
        gameSession.updateScore(score)


        // 정답 여부 확인 및 정답 수 증가
        if (gameQuestion.isCorrectAnswer(gameSetting.maxDistanceForFullScoreKm)) {
            gameSession.incrementCorrectAnswers()
        }

        var isGameSessionCompleted = false
        // 모든 문제 완료 시 게임 완료 처리
        if (gameSession.isAnsweredAllQuestions(gameSetting.maxQuestions)) {
            gameSession.completeGame()
            isGameSessionCompleted = true
        }

        log.info(
            "답안 제출 완료 - memberId: {}, sessionId: {}, questionId: {}, score: {}, distance: {}km",
            member.id, gameSession.id, questionId, score, distanceKm
        )

        return GameQuestionResponse.forAnsweredQuestion(gameQuestion, null, isGameSessionCompleted)
    }

    private fun validateQuestionGeneration(gameSession: GameSession?, gameSetting: GameSetting): Int {
        val nextOrder = gameQuestionRepository.findNextQuestionOrder(gameSession)?.minus(1) ?: 1

        if (nextOrder > gameSetting.maxQuestions) {
            throw ConflictException("모든 문제가 완료되었습니다.")
        }
        return nextOrder
    }
}
