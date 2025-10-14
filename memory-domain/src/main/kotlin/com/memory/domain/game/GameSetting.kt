package com.memory.domain.game

import com.memory.domain.BaseTimeEntity
import jakarta.persistence.*

@Entity
@Table(name = "game_setting")
class GameSetting(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    var gameMode: GameMode,

    @Column(nullable = false)
    var maxQuestions: Int = 10,

    @Column(nullable = false)
    var timeLimitSeconds: Int = 60, // 문제당 제한시간

    @Column(nullable = false)
    var maxDistanceForFullScoreKm: Int = 100, // 만점 기준 거리

    @Column(length = 500)
    var scoringFormula: String = "MAX(1000 - floor((distance_km - 100) / 10))", // 점수 계산 공식

    @Column(nullable = false)
    var isActive: Boolean = true
) : BaseTimeEntity()
