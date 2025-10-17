package com.memory.dto.game.response

import com.memory.domain.game.GameQuestion
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

class GameQuestionResponse {
    private val id: Long?
    private val sessionId: Long?
    private val memoryId: Long?
    private val questionOrder: Int

    private val memoryImageUrls: MutableList<String?>?
    private val encryptCorrectLatitude: String?
    private val encryptCorrectLongitude: String?

    private val playerLatitude: BigDecimal?
    private val playerLongitude: BigDecimal?
    private val distanceKm: BigDecimal?
    private val score: Int?
    private val timeTakenSeconds: Int?
    private val answeredAt: LocalDateTime?

    private val correctLatitude: BigDecimal?
    private val correctLongitude: BigDecimal?
    private val correctLocationName: String?
    private val isGameSessionCompleted: Boolean

    private val createDate: LocalDateTime?

    constructor(gameQuestion: GameQuestion, memoryImageUrls: MutableList<String?>?) {
        this.id = gameQuestion.id
        this.sessionId = gameQuestion.gameSession.id
        this.memoryId = if (gameQuestion.memory != null) gameQuestion.memory!!.id else null
        this.questionOrder = gameQuestion.questionOrder
        this.memoryImageUrls = memoryImageUrls
        val encryptLatitude = Base64.getEncoder().encodeToString(gameQuestion.correctLatitude.toString().toByteArray())
        val encryptLongitude =
            Base64.getEncoder().encodeToString(gameQuestion.correctLongitude.toString().toByteArray())
        this.encryptCorrectLatitude = encryptLatitude
        this.encryptCorrectLongitude = encryptLongitude


        // 답안 정보 (답안 제출 전이므로 null)
        this.playerLatitude = null
        this.playerLongitude = null
        this.distanceKm = null
        this.score = null
        this.timeTakenSeconds = null
        this.answeredAt = null


        // 정답 정보 (답안 제출 전이므로 숨김)
        this.correctLatitude = null
        this.correctLongitude = null
        this.correctLocationName = null
        this.isGameSessionCompleted = false

        this.createDate = gameQuestion.createDate
    }

    // 답안 제출 후 전체 정보 포함 생성자
    constructor(
        gameQuestion: GameQuestion,
        memoryImageUrls: MutableList<String?>?,
        includeAnswer: Boolean,
        isGameSessionCompleted: Boolean
    ) {
        this.id = gameQuestion.id
        this.sessionId = gameQuestion.gameSession.id
        this.memoryId = if (gameQuestion.memory != null) gameQuestion.memory!!.id else null
        this.questionOrder = gameQuestion.questionOrder
        this.memoryImageUrls = memoryImageUrls


        // 플레이어 답안 정보
        this.playerLatitude = gameQuestion.playerLatitude
        this.playerLongitude = gameQuestion.playerLongitude
        this.distanceKm = gameQuestion.distanceKm
        this.score = gameQuestion.score
        this.timeTakenSeconds = gameQuestion.timeTakenSeconds
        this.answeredAt = gameQuestion.answeredAt
        val encryptLatitude = Base64.getEncoder().encodeToString(gameQuestion.correctLatitude.toString().toByteArray())
        val encryptLongitude =
            Base64.getEncoder().encodeToString(gameQuestion.correctLongitude.toString().toByteArray())
        this.encryptCorrectLatitude = encryptLatitude
        this.encryptCorrectLongitude = encryptLongitude

        // 정답 정보 (includeAnswer가 true일 때만)
        this.correctLatitude = if (includeAnswer) gameQuestion.correctLatitude else null
        this.correctLongitude = if (includeAnswer) gameQuestion.correctLongitude else null
        this.correctLocationName = if (includeAnswer) gameQuestion.correctLocationName else null
        this.isGameSessionCompleted = isGameSessionCompleted

        this.createDate = gameQuestion.createDate
    }

    companion object {
        fun forQuestion(gameQuestion: GameQuestion, memoryImageUrls: MutableList<String?>?): GameQuestionResponse {
            return GameQuestionResponse(gameQuestion, memoryImageUrls)
        }

        fun forAnsweredQuestion(
            gameQuestion: GameQuestion,
            memoryImageUrls: MutableList<String?>?,
            isGameSessionCompleted: Boolean
        ): GameQuestionResponse {
            return GameQuestionResponse(gameQuestion, memoryImageUrls, true, isGameSessionCompleted)
        }
    }
}
