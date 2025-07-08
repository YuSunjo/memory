package com.memory.domain.game;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString(exclude = "member")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameRanking extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameMode gameMode;

    // 개인 최고 기록
    @Column(nullable = false)
    private Integer bestScore = 0;

    @Column(precision = 5, scale = 2, nullable = false)
    private BigDecimal bestAccuracy = BigDecimal.ZERO; // 정확도 (%)

    @Column(precision = 8, scale = 2, nullable = false)
    private BigDecimal bestAvgDistanceKm = BigDecimal.ZERO;

    // 전체 통계
    @Column(nullable = false)
    private Integer totalGamesPlayed = 0;

    @Column(nullable = false)
    private Long totalScore = 0L;

    @Column(nullable = false)
    private Integer totalQuestionsAnswered = 0;

    // 랭킹 점수 (주간/월간 계산용)
    @Column(nullable = false)
    private Integer weeklyScore = 0;

    @Column(nullable = false)
    private Integer monthlyScore = 0;

    private LocalDateTime lastPlayedAt;

}
