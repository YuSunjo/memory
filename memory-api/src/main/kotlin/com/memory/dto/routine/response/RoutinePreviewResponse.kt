package com.memory.dto.routine.response

import com.memory.domain.routine.Routine
import lombok.Builder
import lombok.Getter
import java.time.LocalDate

@Getter
@Builder
class RoutinePreviewResponse(
    val routineId: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val targetDate: LocalDate? = null,
    val isPreview: Boolean? = false,
) {
    companion object {
        @JvmStatic
        fun from(routine: Routine, targetDate: LocalDate?): RoutinePreviewResponse? {
            return RoutinePreviewResponse(
                routineId = routine.id,
                title = routine.title,
                content = routine.content,
                targetDate = targetDate,
                isPreview = true
            )
        }
    }
}
