package com.memory.dto.diary

import com.memory.domain.diary.Diary
import com.memory.domain.member.Member
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

class DiaryRequest {

    data class Create(
        @field:NotBlank(message = "제목은 필수 입력값입니다.")
        val title: String,

        val content: String?,

        @field:NotNull(message = "날짜는 필수 입력값입니다.")
        val date: LocalDate,

        val mood: String?,
        val weather: String?
    ) {
        fun toEntity(member: Member): Diary =
            Diary.create(title, content, date, mood, weather, member)
    }

    data class Update(
        @field:NotBlank(message = "제목은 필수 입력값입니다.")
        val title: String,

        val content: String?,

        @field:NotNull(message = "날짜는 필수 입력값입니다.")
        val date: LocalDate,

        val mood: String?,
        val weather: String?
    )

    data class GetByDateRange(
        val startDate: LocalDate,
        val endDate: LocalDate
    )
}