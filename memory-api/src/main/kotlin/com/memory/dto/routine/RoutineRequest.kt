package com.memory.dto.routine

import com.memory.domain.common.repeat.RepeatSetting
import com.memory.domain.common.repeat.RepeatType
import com.memory.domain.member.Member
import com.memory.domain.routine.Routine
import java.time.LocalDate

class RoutineRequest {

    data class Create(
        var title: String? = null,
        var content: String? = null,
        var repeatType: RepeatType,
        var interval: Int? = null,
        var startDate: LocalDate? = null,
        var endDate: LocalDate? = null
    ) {
        fun toEntity(member: Member): Routine =
            Routine.create(
                title ?: "",
                content ?: "",
                member,
                toRepeatSetting()
            )

        fun toRepeatSetting(): RepeatSetting =
            RepeatSetting.of(repeatType, interval, startDate, endDate)
    }

    data class Update(
        var title: String? = null,
        var content: String? = null,
        var repeatType: RepeatType,
        var interval: Int? = null,
        var startDate: LocalDate? = null,
        var endDate: LocalDate? = null
    ) {
        fun toRepeatSetting(): RepeatSetting =
            RepeatSetting.of(repeatType, interval, startDate, endDate)
    }
}