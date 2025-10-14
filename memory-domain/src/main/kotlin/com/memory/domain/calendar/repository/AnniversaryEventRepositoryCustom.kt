package com.memory.domain.calendar.repository

import com.memory.domain.calendar.AnniversaryEvent
import com.memory.domain.member.Member
import java.time.LocalDateTime

interface AnniversaryEventRepositoryCustom {

    fun findByMemberAndStartDateTimeBetween(member: Member?, startDate: LocalDateTime?, endDate: LocalDateTime?): List<AnniversaryEvent>

    fun findByMemberAndIsDdayTrue(member: Member?): List<AnniversaryEvent>

}
