package com.memory.domain.calendar.repository

import com.memory.domain.calendar.RelationshipEvent
import com.memory.domain.member.Member
import java.time.LocalDateTime

interface RelationshipEventRepositoryCustom {

    fun findByMemberAndStartDateTimeBetween(member: Member?, startDate: LocalDateTime?, endDate: LocalDateTime?): List<RelationshipEvent>

    fun findByMemberAndFutureEvents(member: Member?): List<RelationshipEvent>
}
