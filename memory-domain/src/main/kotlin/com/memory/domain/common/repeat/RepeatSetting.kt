package com.memory.domain.common.repeat

import jakarta.persistence.Embeddable
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Embeddable
class RepeatSetting(
    @Enumerated(EnumType.STRING)
    var repeatType: RepeatType? = null,
    var interval: Int? = null,        // 반복 간격 (예: 2주마다, 3일마다)
    var startDate: LocalDate? = null, // 반복 시작일
    var endDate: LocalDate? = null    // 반복 종료일 (null이면 무한 반복)
) {
    companion object {
        @JvmStatic
        fun none(): RepeatSetting =
            RepeatSetting(RepeatType.NONE, null, null, null)

        @JvmStatic
        fun of(
            repeatType: RepeatType,
            interval: Int?,
            startDate: LocalDate?,
            endDate: LocalDate?
        ): RepeatSetting =
            if (repeatType == RepeatType.NONE) none()
            else RepeatSetting(repeatType, interval, startDate, endDate)
    }

    // 특정 날짜에 이 설정이 적용되는지 확인
    fun isApplicableOn(targetDate: LocalDate): Boolean {
        val type = repeatType ?: return false
        val start = startDate ?: return false

        // 시작일 이전이면 적용되지 않음
        if (targetDate.isBefore(start)) return false

        // 종료일이 설정되어 있고 종료일 이후면 적용되지 않음
        if (endDate != null && targetDate.isAfter(endDate)) return false

        val step = interval ?: 1

        return when (type) {
            RepeatType.NONE -> false

            RepeatType.DAILY -> {
                val daysBetween = ChronoUnit.DAYS.between(start, targetDate)
                daysBetween % step == 0L
            }

            RepeatType.WEEKLY -> {
                if (targetDate.dayOfWeek != start.dayOfWeek) return false
                val weeksBetween = ChronoUnit.WEEKS.between(start, targetDate)
                weeksBetween % step == 0L
            }

            RepeatType.MONTHLY -> {
                if (targetDate.dayOfMonth != start.dayOfMonth) return false
                val monthsBetween = ChronoUnit.MONTHS.between(start, targetDate)
                monthsBetween % step == 0L
            }

            RepeatType.YEARLY -> {
                if (targetDate.monthValue != start.monthValue ||
                    targetDate.dayOfMonth != start.dayOfMonth
                ) return false
                val yearsBetween = ChronoUnit.YEARS.between(start, targetDate)
                yearsBetween % step == 0L
            }
        }
    }
}