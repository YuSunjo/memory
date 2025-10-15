package com.memory.dto.diary.response

import com.memory.domain.diary.Diary
import com.memory.dto.member.response.MemberResponse
import java.time.LocalDate
import java.time.LocalDateTime

data class DiaryResponse private constructor(
    val id: Long?,
    val title: String?,
    val content: String?,      // 자바의 String은 null 가능성이 있어 ? 처리
    val date: LocalDate?,
    val mood: String?,
    val weather: String?,
    val member: MemberResponse?,
    val createDate: LocalDateTime?,
    val updateDate: LocalDateTime?,
) {
    companion object {
        @JvmStatic
        fun from(diary: Diary?): DiaryResponse? {
            if (diary == null) return null

            return DiaryResponse(
                id = diary.id,
                title = diary.title,
                content = diary.content,
                date = diary.date,
                mood = diary.mood,
                weather = diary.weather,
                member = diary.member?.let { MemberResponse.from(it) },
                createDate = diary.createDate,
                updateDate = diary.updateDate
            )
        }
    }
}