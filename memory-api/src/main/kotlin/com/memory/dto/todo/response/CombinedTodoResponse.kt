package com.memory.dto.todo.response

import com.memory.dto.routine.response.RoutinePreviewResponse

data class CombinedTodoResponse(
    val actualTodos: MutableList<TodoResponse?>? = null,
    val routinePreviews: MutableList<RoutinePreviewResponse?>? = null,
) {
    companion object {
        @JvmStatic
        fun of(
            actualTodos: MutableList<TodoResponse?>? = null,
            routinePreviews: MutableList<RoutinePreviewResponse?>? = null,
        ): CombinedTodoResponse {
            return CombinedTodoResponse(actualTodos, routinePreviews)
        }
    }
}
