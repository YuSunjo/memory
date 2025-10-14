package com.memory.domain.calendar.repository

import com.memory.domain.calendar.PersonalEvent
import com.memory.domain.member.Member
import java.time.LocalDateTime

interface PersonalEventRepositoryCustom {

    fun findByMemberAndStartDateTimeBetween(member: Member?, startDate: LocalDateTime?, endDate: LocalDateTime?): List<PersonalEvent>

    fun findByMemberAndFutureEvents(member: Member?): List<PersonalEvent>

}
