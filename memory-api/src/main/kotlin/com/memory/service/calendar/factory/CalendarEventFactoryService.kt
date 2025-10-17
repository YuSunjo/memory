package com.memory.service.calendar.factory

import com.memory.dto.calendar.CalendarEventRequest
import com.memory.dto.calendar.response.BaseCalendarEventResponse
import java.time.LocalDateTime

interface CalendarEventFactoryService {
    fun createCalendarEvent(memberId: Long?, request: CalendarEventRequest.Create): BaseCalendarEventResponse?

    fun updateCalendarEvent(
        memberId: Long?,
        eventId: Long,
        request: CalendarEventRequest.Update
    ): BaseCalendarEventResponse?

    fun getCalendarEventsByDateRange(
        memberId: Long?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): MutableList<BaseCalendarEventResponse?>?

    fun getCalendarEventsWithDday(memberId: Long?): MutableList<BaseCalendarEventResponse?>?
}
