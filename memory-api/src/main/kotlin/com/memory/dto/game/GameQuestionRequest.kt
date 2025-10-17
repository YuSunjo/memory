package com.memory.dto.game;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.math.BigDecimal;

public class GameQuestionRequest {

    @Getter
    public static class SubmitAnswer {
        @NotNull(message = "플레이어 위도는 필수 입력값입니다.")
        @DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
        @DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다.")
        private BigDecimal playerLatitude;

        @NotNull(message = "플레이어 경도는 필수 입력값입니다.")
        @DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
        @DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다.")
        private BigDecimal playerLongitude;

        @NotNull(message = "소요 시간은 필수 입력값입니다.")
        @Min(value = 0, message = "소요 시간은 0 이상이어야 합니다.")
        private Integer timeTakenSeconds;

        public SubmitAnswer(BigDecimal playerLatitude, BigDecimal playerLongitude, Integer timeTakenSeconds) {
            this.playerLatitude = playerLatitude;
            this.playerLongitude = playerLongitude;
            this.timeTakenSeconds = timeTakenSeconds;
        }
    }
}
