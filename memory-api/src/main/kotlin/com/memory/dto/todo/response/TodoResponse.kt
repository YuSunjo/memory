package com.memory.dto.todo.response

import com.memory.domain.todo.Todo
import com.memory.dto.member.response.MemberResponse
import com.memory.dto.routine.response.RoutinePreviewResponse
import java.time.LocalDateTime

data class TodoResponse private constructor(
    val id: Long?,
    val title: String?,
    val content: String?,
    val dueDate: LocalDateTime?,
    val completed: Boolean,
    val isRoutine: Boolean,
    val routineId: Long?,
    val member: MemberResponse?,
    val createDate: LocalDateTime?,
    val updateDate: LocalDateTime?,
) {
    companion object {
        @JvmStatic
        fun from(todo: Todo?): TodoResponse? {
            if (todo == null) return null
            return TodoResponse(
                id = todo.id,
                title = todo.title,
                content = todo.content,
                dueDate = todo.dueDate,
                completed = todo.completed,
                isRoutine = todo.isRoutine,
                routineId = todo.routine?.id,
                member = todo.member ?.let { MemberResponse.from(it) },
                createDate = todo.createDate,
                updateDate = todo.updateDate
            )
        }
    }

    fun isConvertRoutine(routine: RoutinePreviewResponse?): Boolean {
        if (routine == null || !isRoutine) return false
        return dueDate?.toLocalDate() == routine.targetDate && routineId == routine.routineId
    }
}
