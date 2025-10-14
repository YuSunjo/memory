package com.memory.domain.game

import com.memory.domain.BaseTimeEntity
import com.memory.domain.memory.Memory
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "game_question")
class GameQuestion private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id", nullable = false)
    var gameSession: GameSession,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id")
    var memory: Memory? = null,

    @Column(nullable = false)
    var questionOrder: Int,

    // 정답 위치
    @Column(precision = 10, scale = 8, nullable = false)
    var correctLatitude: BigDecimal,

    @Column(precision = 11, scale = 8, nullable = false)
    var correctLongitude: BigDecimal,

    var correctLocationName: String? = null,

    // 플레이어 답안
    @Column(precision = 10, scale = 8)
    var playerLatitude: BigDecimal? = null,

    @Column(precision = 11, scale = 8)
    var playerLongitude: BigDecimal? = null,

    @Column(precision = 8, scale = 2)
    var distanceKm: BigDecimal? = null,

    var score: Int? = null,

    var timeTakenSeconds: Int? = null,

    var answeredAt: LocalDateTime? = null
) : BaseTimeEntity() {

    constructor(gameSession: GameSession?, memory: Memory?, questionOrder: Int?,
        correctLatitude: BigDecimal?, correctLongitude: BigDecimal?,
        correctLocationName: String?) : this(
            id = null,
            gameSession = gameSession!!,
            memory = memory,
            questionOrder = questionOrder!!,
            correctLatitude = correctLatitude!!,
            correctLongitude = correctLongitude!!,
            correctLocationName = correctLocationName
        )

    companion object {
        @JvmStatic
        fun init(
            gameSession: GameSession,
            memory: Memory? = null,
            questionOrder: Int,
            correctLatitude: BigDecimal,
            correctLongitude: BigDecimal,
            correctLocationName: String?
        ): GameQuestion {
            return GameQuestion(
                gameSession = gameSession,
                memory = memory,
                questionOrder = questionOrder,
                correctLatitude = correctLatitude,
                correctLongitude = correctLongitude,
                correctLocationName = correctLocationName
            )
        }
    }

    fun submitAnswer(
        playerLatitude: BigDecimal,
        playerLongitude: BigDecimal,
        distanceKm: BigDecimal,
        score: Int,
        timeTakenSeconds: Int
    ) {
        this.playerLatitude = playerLatitude
        this.playerLongitude = playerLongitude
        this.distanceKm = distanceKm
        this.score = score
        this.timeTakenSeconds = timeTakenSeconds
        this.answeredAt = LocalDateTime.now()
    }

    fun updateGameSession(gameSession: GameSession) {
        this.gameSession = gameSession
    }

    fun isAnswered(): Boolean = answeredAt != null

    fun isCorrectAnswer(maxDistanceForFullScore: Int): Boolean {
        return distanceKm?.let { it.toDouble() <= maxDistanceForFullScore } ?: false
    }
}
