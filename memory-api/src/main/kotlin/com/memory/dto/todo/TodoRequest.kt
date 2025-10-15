package com.memory.dto.todo

import com.memory.domain.member.Member
import com.memory.domain.todo.Todo
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate
import java.time.LocalDateTime

class TodoRequest {

    data class Create(
        @field:NotBlank(message = "제목은 필수 입력값입니다.")
        val title: String,

        val content: String?,

        @field:NotNull(message = "마감일시는 필수 입력값입니다.")
        val dueDate: LocalDateTime
    ) {
        fun toEntity(member: Member): Todo =
            Todo.create(title, content, dueDate, member)
    }

    data class Update(
        @field:NotBlank(message = "제목은 필수 입력값입니다.")
        val title: String,

        // 자바에서 final String content → 동일하게 불변, 필요 시 null 허용
        val content: String?,

        @field:NotNull(message = "마감일시는 필수 입력값입니다.")
        val dueDate: LocalDateTime
    )

    data class UpdateStatus(
        @field:NotNull(message = "완료 상태는 필수 입력값입니다.")
        val completed: Boolean
    )

    data class ConvertRoutine(
        @field:NotNull(message = "루틴 ID는 필수 입력값입니다.")
        val routineId: Long,

        @field:NotNull(message = "대상 날짜는 필수 입력값입니다.")
        val targetDate: LocalDate
    )
}
