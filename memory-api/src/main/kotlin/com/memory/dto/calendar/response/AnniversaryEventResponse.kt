package com.memory.dto.calendar.response

import com.memory.domain.calendar.AnniversaryEvent
import com.memory.domain.calendar.BaseCalendarEvent
import com.memory.dto.member.response.MemberResponse
import com.memory.dto.member.response.MemberResponse.Companion.from
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class AnniversaryEventResponse(
    id: Long?,
    title: String?,
    description: String?,
    startDateTime: LocalDateTime?,
    endDateTime: LocalDateTime?,
    location: String?,
    member: MemberResponse?,
    createDate: LocalDateTime?,
    relatedMember: MemberResponse?,
    isDday: Boolean?, dday: Int?
) : BaseCalendarEventResponse(id, title, description, startDateTime, endDateTime, location, member, createDate, dday) {
    companion object {
        fun from(event: BaseCalendarEvent?): AnniversaryEventResponse {
            require(event is AnniversaryEvent) { "Event is not an AnniversaryEvent" }

            val dday: Int = calculateDday(event.startDateTime)

            return AnniversaryEventResponse(
                event.id,
                event.title,
                event.description,
                event.startDateTime,
                event.endDateTime,
                event.location,
                from(event.member),
                event.createDate,
                from(event.relationship.relatedMember),
                event.isDday,
                dday
            )
        }

        private fun calculateDday(eventDateTime: LocalDateTime): Int {
            val today = LocalDate.now()
            val eventDate = eventDateTime.toLocalDate()
            return ChronoUnit.DAYS.between(today, eventDate).toInt()
        }
    }
}
