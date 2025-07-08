package com.memory.domain.game;

import com.memory.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSetting extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true)
    private GameMode gameMode;

    @Column(nullable = false)
    private Integer maxQuestions = 10;

    @Column(nullable = false)
    private Integer timeLimitSeconds = 60; // 문제당 제한시간

    @Column(nullable = false)
    private Integer maxDistanceForFullScoreKm = 1; // 만점 기준 거리

    @Column(length = 500)
    private String scoringFormula = "MAX(0, 1000 - (distance_km * 100))"; // 점수 계산 공식

    @Column(nullable = false)
    private Boolean isActive = true;

}
