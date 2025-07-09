package com.memory.dto.game.response;

import com.memory.domain.game.GameQuestion;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

@Getter
public class GameQuestionResponse {

    private final Long id;
    private final Long sessionId;
    private final Long memoryId;
    private final Integer questionOrder;
    
    private final List<String> memoryImageUrls;
    private final String encryptCorrectLatitude;
    private final String encryptCorrectLongitude;

    private final BigDecimal playerLatitude;
    private final BigDecimal playerLongitude;
    private final BigDecimal distanceKm;
    private final Integer score;
    private final Integer timeTakenSeconds;
    private final LocalDateTime answeredAt;
    
    private final BigDecimal correctLatitude;
    private final BigDecimal correctLongitude;
    private final String correctLocationName;
    
    private final LocalDateTime createDate;

    public GameQuestionResponse(GameQuestion gameQuestion, List<String> memoryImageUrls) {
        this.id = gameQuestion.getId();
        this.sessionId = gameQuestion.getGameSession().getId();
        this.memoryId = gameQuestion.getMemory() != null ? gameQuestion.getMemory().getId() : null;
        this.questionOrder = gameQuestion.getQuestionOrder();
        this.memoryImageUrls = memoryImageUrls;
        String encryptLatitude = Base64.getEncoder().encodeToString(gameQuestion.getCorrectLatitude().toString().getBytes());
        String encryptLongitude = Base64.getEncoder().encodeToString(gameQuestion.getCorrectLongitude().toString().getBytes());
        this.encryptCorrectLatitude = encryptLatitude;
        this.encryptCorrectLongitude = encryptLongitude;
        
        // 답안 정보 (답안 제출 전이므로 null)
        this.playerLatitude = null;
        this.playerLongitude = null;
        this.distanceKm = null;
        this.score = null;
        this.timeTakenSeconds = null;
        this.answeredAt = null;
        
        // 정답 정보 (답안 제출 전이므로 숨김)
        this.correctLatitude = null;
        this.correctLongitude = null;
        this.correctLocationName = null;
        
        this.createDate = gameQuestion.getCreateDate();
    }

    // 답안 제출 후 전체 정보 포함 생성자
    public GameQuestionResponse(GameQuestion gameQuestion, List<String> memoryImageUrls, boolean includeAnswer) {
        this.id = gameQuestion.getId();
        this.sessionId = gameQuestion.getGameSession().getId();
        this.memoryId = gameQuestion.getMemory() != null ? gameQuestion.getMemory().getId() : null;
        this.questionOrder = gameQuestion.getQuestionOrder();
        this.memoryImageUrls = memoryImageUrls;
        
        // 플레이어 답안 정보
        this.playerLatitude = gameQuestion.getPlayerLatitude();
        this.playerLongitude = gameQuestion.getPlayerLongitude();
        this.distanceKm = gameQuestion.getDistanceKm();
        this.score = gameQuestion.getScore();
        this.timeTakenSeconds = gameQuestion.getTimeTakenSeconds();
        this.answeredAt = gameQuestion.getAnsweredAt();
        String encryptLatitude = Base64.getEncoder().encodeToString(gameQuestion.getCorrectLatitude().toString().getBytes());
        String encryptLongitude = Base64.getEncoder().encodeToString(gameQuestion.getCorrectLongitude().toString().getBytes());
        this.encryptCorrectLatitude = encryptLatitude;
        this.encryptCorrectLongitude = encryptLongitude;

        // 정답 정보 (includeAnswer가 true일 때만)
        this.correctLatitude = includeAnswer ? gameQuestion.getCorrectLatitude() : null;
        this.correctLongitude = includeAnswer ? gameQuestion.getCorrectLongitude() : null;
        this.correctLocationName = includeAnswer ? gameQuestion.getCorrectLocationName() : null;
        
        this.createDate = gameQuestion.getCreateDate();
    }

    public static GameQuestionResponse forQuestion(GameQuestion gameQuestion, List<String> memoryImageUrls) {
        return new GameQuestionResponse(gameQuestion, memoryImageUrls);
    }

    public static GameQuestionResponse forAnsweredQuestion(GameQuestion gameQuestion, List<String> memoryImageUrls) {
        return new GameQuestionResponse(gameQuestion, memoryImageUrls, true);
    }
}
