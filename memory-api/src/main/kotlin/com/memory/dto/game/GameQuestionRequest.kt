package com.memory.dto.game

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

class GameQuestionRequest {

    data class SubmitAnswer(
        @field:NotNull(message = "플레이어 위도는 필수 입력값입니다.")
        @field:DecimalMin(value = "-90.0", message = "위도는 -90.0 이상이어야 합니다.")
        @field:DecimalMax(value = "90.0", message = "위도는 90.0 이하여야 합니다.")
        val playerLatitude: BigDecimal,

        @field:NotNull(message = "플레이어 경도는 필수 입력값입니다.")
        @field:DecimalMin(value = "-180.0", message = "경도는 -180.0 이상이어야 합니다.")
        @field:DecimalMax(value = "180.0", message = "경도는 180.0 이하여야 합니다.")
        val playerLongitude: BigDecimal,

        @field:NotNull(message = "소요 시간은 필수 입력값입니다.")
        @field:Min(value = 0, message = "소요 시간은 0 이상이어야 합니다.")
        val timeTakenSeconds: Int
    )
}