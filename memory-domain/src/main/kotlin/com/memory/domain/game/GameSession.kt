package com.memory.domain.game

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "game_session")
class GameSession private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id")
    var targetMember: Member? = null, // FRIEND_MEMORIES 모드용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var gameMode: GameMode,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: GameSessionStatus = GameSessionStatus.IN_PROGRESS,

    @Column(nullable = false)
    var totalScore: Int = 0,

    @Column(nullable = false)
    var totalQuestions: Int = 0,

    @Column(nullable = false)
    var correctAnswers: Int = 0,

    @Column(nullable = false)
    var startTime: LocalDateTime,

    var endTime: LocalDateTime? = null,

    @OneToMany(mappedBy = "gameSession", cascade = [CascadeType.ALL], orphanRemoval = true)
    var gameQuestions: MutableList<GameQuestion> = mutableListOf()
) : BaseTimeEntity() {

    companion object {
        @JvmStatic
        fun gameSessionInit(member: Member, gameMode: GameMode): GameSession {
            return GameSession(
                member = member,
                gameMode = gameMode,
                startTime = LocalDateTime.now()
            )
        }
    }

    fun addGameQuestion(gameQuestion: GameQuestion) {
        gameQuestions.add(gameQuestion)
        gameQuestion.updateGameSession(this)
        totalQuestions = gameQuestions.size
    }

    fun giveUpGameSession() {
        status = GameSessionStatus.ABANDONED
        endTime = LocalDateTime.now()
    }

    fun getAccuracy(): Double {
        if (totalQuestions == 0) return 0.0
        return correctAnswers.toDouble() / totalQuestions * 100
    }

    fun isInProgress(): Boolean = status == GameSessionStatus.IN_PROGRESS

    fun isOwner(member: Member): Boolean = this.member.id == member.id

    fun updateScore(questionScore: Int) {
        totalScore += questionScore
    }

    fun incrementCorrectAnswers() {
        correctAnswers++
    }

    fun completeGame() {
        status = GameSessionStatus.COMPLETED
        endTime = LocalDateTime.now()
    }

    fun isAnsweredAllQuestions(maxQuestions: Int): Boolean {
        return gameQuestions.size >= maxQuestions &&
                gameQuestions.all { it.answeredAt != null }
    }
}
