package com.memory.dto.calendar

import com.memory.domain.calendar.AnniversaryEvent
import com.memory.domain.calendar.AnniversaryEvent.Companion.create
import com.memory.domain.calendar.CalendarEventType
import com.memory.domain.calendar.PersonalEvent
import com.memory.domain.calendar.PersonalEvent.Companion.create
import com.memory.domain.calendar.RelationshipEvent
import com.memory.domain.member.Member
import com.memory.domain.relationship.Relationship
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime

class CalendarEventRequest {

    data class Create(
        override var title: String,
        override var description: String?,
        override var startDateTime: LocalDateTime,
        override var endDateTime: LocalDateTime?,
        override var location: String?,
        @field:NotNull(message = "이벤트 타입은 필수 입력값입니다.")
        val eventType: CalendarEventType,
        val isDday: Boolean? = null
    ) : BaseCalendarEventRequest.Create(
        title = title,
        description = description,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        location = location
    ) {
        fun toPersonalEvent(member: Member): PersonalEvent =
            create(title, description, startDateTime, endDateTime, location, member)

        fun toRelationshipEvent(member: Member, relationship: Relationship): RelationshipEvent =
            RelationshipEvent.create(title, description, startDateTime, endDateTime, location, member, relationship)

        fun toAnniversaryEvent(member: Member, relationship: Relationship): AnniversaryEvent =
            create(title, description, startDateTime, endDateTime, location, member, relationship,
                isDday == true
            )
    }

    data class Update(
        override var title: String,
        override var description: String?,
        override var startDateTime: LocalDateTime,
        override var endDateTime: LocalDateTime?,
        override var location: String?,
        @field:NotNull(message = "이벤트 타입은 필수 입력값입니다.")
        val eventType: CalendarEventType,
        val isDday: Boolean? = null
    ) : BaseCalendarEventRequest.Update(
        title = title,
        description = description,
        startDateTime = startDateTime,
        endDateTime = endDateTime,
        location = location
    )
}