package com.memory.domain.diary.repository

import com.memory.domain.diary.Diary
import com.memory.domain.member.Member
import java.time.LocalDate

interface DiaryRepositoryCustom {

    fun findActiveDiariesByMemberAndDateBetween(member: Member?, startDate: LocalDate?, endDate: LocalDate?): List<Diary>
}
