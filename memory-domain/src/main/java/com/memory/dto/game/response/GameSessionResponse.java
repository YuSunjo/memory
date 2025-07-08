package com.memory.dto.game.response;

import com.memory.domain.game.GameMode;
import com.memory.domain.game.GameSession;
import com.memory.domain.game.GameSessionStatus;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameSessionResponse {
    
    private final Long id;
    private final Long memberId;
    private final Long targetMemberId;
    private final GameMode gameMode;
    private final GameSessionStatus status;
    private final Integer totalScore;
    private final Integer totalQuestions;
    private final Integer correctAnswers;
    private final Double accuracy;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final LocalDateTime createDate;

    public GameSessionResponse(GameSession gameSession) {
        this.id = gameSession.getId();
        this.memberId = gameSession.getMember().getId();
        this.targetMemberId = gameSession.getTargetMember() != null ? gameSession.getTargetMember().getId() : null;
        this.gameMode = gameSession.getGameMode();
        this.status = gameSession.getStatus();
        this.totalScore = gameSession.getTotalScore();
        this.totalQuestions = gameSession.getTotalQuestions();
        this.correctAnswers = gameSession.getCorrectAnswers();
        this.accuracy = gameSession.getAccuracy();
        this.startTime = gameSession.getStartTime();
        this.endTime = gameSession.getEndTime();
        this.createDate = gameSession.getCreateDate();
    }

    public static GameSessionResponse from(GameSession gameSession) {
        return new GameSessionResponse(gameSession);
    }
}
