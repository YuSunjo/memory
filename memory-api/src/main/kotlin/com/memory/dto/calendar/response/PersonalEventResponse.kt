package com.memory.dto.calendar.response

import com.memory.domain.calendar.BaseCalendarEvent
import com.memory.domain.calendar.PersonalEvent
import com.memory.dto.member.response.MemberResponse
import com.memory.dto.member.response.MemberResponse.Companion.from
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class PersonalEventResponse(
    id: Long?,
    title: String?,
    description: String?,
    startDateTime: LocalDateTime?,
    endDateTime: LocalDateTime?,
    location: String?,
    member: MemberResponse?,
    createDate: LocalDateTime?,
    dday: Int?
) : BaseCalendarEventResponse(id, title, description, startDateTime, endDateTime, location, member, createDate, dday) {
    companion object {
        fun from(event: BaseCalendarEvent?): PersonalEventResponse {
            require(event is PersonalEvent) { "Event is not a PersonalEvent" }

            val dday: Int = calculateDday(event.startDateTime)

            return PersonalEventResponse(
                event.id,
                event.title,
                event.description,
                event.startDateTime,
                event.endDateTime,
                event.location,
                from(event.member),
                event.createDate,
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
