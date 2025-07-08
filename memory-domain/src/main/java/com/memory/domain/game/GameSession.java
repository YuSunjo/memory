package com.memory.domain.game;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@ToString(exclude = {"member", "targetMember", "gameQuestions"})
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class GameSession extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_member_id")
    private Member targetMember; // FRIEND_MEMORIES 모드용

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameMode gameMode;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GameSessionStatus status = GameSessionStatus.IN_PROGRESS;

    @Column(nullable = false)
    private Integer totalScore = 0;

    @Column(nullable = false)
    private Integer totalQuestions = 0;

    @Column(nullable = false)
    private Integer correctAnswers = 0;

    @Column(nullable = false)
    private LocalDateTime startTime;

    private LocalDateTime endTime;

    @OneToMany(mappedBy = "gameSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameQuestion> gameQuestions = new ArrayList<>();

    public GameSession(Member member, GameMode gameMode) {
        this.member = member;
        this.gameMode = gameMode;
        this.startTime = LocalDateTime.now();
    }

    public static GameSession gameSessionInit(Member member, GameMode gameMode) {
        return new GameSession(member, gameMode);
    }

    public void addGameQuestion(GameQuestion gameQuestion) {
        this.gameQuestions.add(gameQuestion);
        gameQuestion.updateGameSession(this);
        this.totalQuestions = this.gameQuestions.size();
    }

    public void giveUpGameSession() {
        this.status = GameSessionStatus.ABANDONED;
        this.endTime = LocalDateTime.now();
    }

    public double getAccuracy() {
        if (totalQuestions == 0) return 0.0;
        return (double) correctAnswers / totalQuestions * 100;
    }

    public boolean isInProgress() {
        return status == GameSessionStatus.IN_PROGRESS;
    }
}
