package com.memory.dto.routine.response

import com.memory.domain.routine.Routine
import lombok.Builder
import lombok.Getter
import java.time.LocalDate

@Getter
@Builder
class RoutineResponse(
    val id: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val active: Boolean? = false,
    val repeatType: String? = null,
    val interval: Int? = null,
    val startDate: LocalDate? = null,
    val endDate: LocalDate? = null,
) {


    companion object {
        @JvmStatic
        fun from(routine: Routine): RoutineResponse? {
            return RoutineResponse(
                routine.id,
                routine.title,
                routine.content,
                routine.active,
                routine.repeatSetting.repeatType?.name,
                routine.repeatSetting.interval,
                routine.repeatSetting.startDate,
                routine.repeatSetting.endDate
            )
        }
    }
}
