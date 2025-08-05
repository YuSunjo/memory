package com.memory.dto.game.response;

import com.memory.domain.game.GameMode;
import com.memory.domain.game.GameSetting;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class GameSettingResponse {
    
    private final Long id;
    private final GameMode gameMode;
    private final Integer maxQuestions;
    private final Integer timeLimitSeconds;
    private final Integer maxDistanceForFullScoreKm;
    private final String scoringFormula;
    private final Boolean isActive;
    private final LocalDateTime createDate;
    
    public GameSettingResponse(GameSetting gameSetting) {
        this.id = gameSetting.getId();
        this.gameMode = gameSetting.getGameMode();
        this.maxQuestions = gameSetting.getMaxQuestions();
        this.timeLimitSeconds = gameSetting.getTimeLimitSeconds();
        this.maxDistanceForFullScoreKm = gameSetting.getMaxDistanceForFullScoreKm();
        this.scoringFormula = gameSetting.getScoringFormula();
        this.isActive = gameSetting.getIsActive();
        this.createDate = gameSetting.getCreateDate();
    }
    
    public static GameSettingResponse from(GameSetting gameSetting) {
        return new GameSettingResponse(gameSetting);
    }
}
