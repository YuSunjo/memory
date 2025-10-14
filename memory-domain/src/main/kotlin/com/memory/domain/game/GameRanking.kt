package com.memory.domain.game

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "game_ranking")
class GameRanking(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    var member: Member,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var gameMode: GameMode,

    // 개인 최고 기록
    @Column(nullable = false)
    var bestScore: Int = 0,

    @Column(precision = 5, scale = 2, nullable = false)
    var bestAccuracy: BigDecimal = BigDecimal.ZERO, // 정확도 (%)

    @Column(precision = 8, scale = 2, nullable = false)
    var bestAvgDistanceKm: BigDecimal = BigDecimal.ZERO,

    // 전체 통계
    @Column(nullable = false)
    var totalGamesPlayed: Int = 0,

    @Column(nullable = false)
    var totalScore: Long = 0L,

    @Column(nullable = false)
    var totalQuestionsAnswered: Int = 0,

    // 랭킹 점수 (주간/월간 계산용)
    @Column(nullable = false)
    var weeklyScore: Int = 0,

    @Column(nullable = false)
    var monthlyScore: Int = 0,

    var lastPlayedAt: LocalDateTime? = null
) : BaseTimeEntity()
