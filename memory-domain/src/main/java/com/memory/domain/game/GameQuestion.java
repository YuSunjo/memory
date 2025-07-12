package com.memory.domain.game;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.memory.Memory;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@ToString(exclude = {"gameSession", "memory"})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameQuestion extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_session_id", nullable = false)
    private GameSession gameSession;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id")
    private Memory memory;

    @Column(nullable = false)
    private Integer questionOrder;

    // 정답 위치
    @Column(precision = 10, scale = 8, nullable = false)
    private BigDecimal correctLatitude;

    @Column(precision = 11, scale = 8, nullable = false)
    private BigDecimal correctLongitude;

    private String correctLocationName;

    // 플레이어 답안
    @Column(precision = 10, scale = 8)
    private BigDecimal playerLatitude;

    @Column(precision = 11, scale = 8)
    private BigDecimal playerLongitude;

    @Column(precision = 8, scale = 2)
    private BigDecimal distanceKm;

    private Integer score;

    private Integer timeTakenSeconds;

    private LocalDateTime answeredAt;

    @Builder
    public GameQuestion(GameSession gameSession, Memory memory, Integer questionOrder, 
                       BigDecimal correctLatitude, BigDecimal correctLongitude, 
                       String correctLocationName) {
        this.gameSession = gameSession;
        this.memory = memory;
        this.questionOrder = questionOrder;
        this.correctLatitude = correctLatitude;
        this.correctLongitude = correctLongitude;
        this.correctLocationName = correctLocationName;
    }

    public static GameQuestion init(GameSession gameSession, Memory memory, Integer questionOrder,
                                    BigDecimal correctLatitude, BigDecimal correctLongitude,
                                    String correctLocationName) {
        return new GameQuestion(gameSession, memory, questionOrder, correctLatitude, correctLongitude, correctLocationName);
    }

    public void submitAnswer(BigDecimal playerLatitude, BigDecimal playerLongitude, 
                           BigDecimal distanceKm, Integer score, Integer timeTakenSeconds) {
        this.playerLatitude = playerLatitude;
        this.playerLongitude = playerLongitude;
        this.distanceKm = distanceKm;
        this.score = score;
        this.timeTakenSeconds = timeTakenSeconds;
        this.answeredAt = LocalDateTime.now();
    }

    public void updateGameSession(GameSession gameSession) {
        this.gameSession = gameSession;
    }

    public boolean isAnswered() {
        return this.answeredAt != null;
    }

    public boolean isCorrectAnswer(Integer maxDistanceForFullScore) {
        return this.distanceKm != null && 
               this.distanceKm.doubleValue() <= maxDistanceForFullScore;
    }
}
