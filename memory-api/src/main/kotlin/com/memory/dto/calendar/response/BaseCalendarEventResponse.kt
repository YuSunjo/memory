package com.memory.dto.calendar.response

import com.memory.domain.calendar.BaseCalendarEvent
import com.memory.dto.member.response.MemberResponse
import java.time.LocalDateTime

abstract class BaseCalendarEventResponse protected constructor(
    id: Long?,
    title: String?,
    description: String?,
    startDateTime: LocalDateTime?,
    endDateTime: LocalDateTime?,
    location: String?,
    member: MemberResponse?,
    createDate: LocalDateTime?,
    dday: Int?
) {
    companion object {
        fun from(event: BaseCalendarEvent?): BaseCalendarEventResponse? {
            if (event == null) {
                return null
            }

            return when (event.javaClass.getSimpleName()) {
                "PersonalEvent" -> PersonalEventResponse.Companion.from(event)
                "AnniversaryEvent" -> AnniversaryEventResponse.Companion.from(event)
                "RelationshipEvent" -> RelationshipEventResponse.Companion.from(event)
                else -> throw IllegalArgumentException("Unknown event type: " + event.javaClass.getSimpleName())
            }
        }
    }
}
