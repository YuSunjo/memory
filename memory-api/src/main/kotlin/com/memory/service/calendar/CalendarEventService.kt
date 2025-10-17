package com.memory.service.calendar

import com.memory.domain.calendar.CalendarEventType
import com.memory.dto.calendar.CalendarEventRequest
import com.memory.dto.calendar.response.BaseCalendarEventResponse
import com.memory.service.calendar.factory.CalendarEventFactory
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
@RequiredArgsConstructor
class CalendarEventService(
    private val calendarEventFactory: CalendarEventFactory,

) {
    @Transactional
    fun createCalendarEvent(memberId: Long?, request: CalendarEventRequest.Create): BaseCalendarEventResponse? {
        val calendarEventService = calendarEventFactory.getCalendarEventService(request.getEventType())
        return calendarEventService.createCalendarEvent(memberId, request)
    }

    @Transactional
    fun updateCalendarEvent(
        memberId: Long?,
        eventId: Long,
        request: CalendarEventRequest.Update
    ): BaseCalendarEventResponse? {
        val calendarEventService = calendarEventFactory.getCalendarEventService(request.getEventType())
        return calendarEventService.updateCalendarEvent(memberId, eventId, request)
    }

    @Transactional(readOnly = true)
    fun getCalendarEventsByDateRange(
        memberId: Long?,
        startDate: LocalDate,
        endDate: LocalDate
    ): MutableList<BaseCalendarEventResponse?> {
        // LocalDate를 LocalDateTime으로 변환 (시작일은 00:00:00, 종료일은 23:59:59)
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(LocalTime.MAX)

        val allEvents: MutableList<BaseCalendarEventResponse?> = ArrayList<BaseCalendarEventResponse?>()

        // 모든 이벤트 타입에 대해 조회
        for (eventType in CalendarEventType.entries) {
            val calendarEventService = calendarEventFactory.getCalendarEventService(eventType)
            val events = calendarEventService.getCalendarEventsByDateRange(memberId, startDateTime, endDateTime)
            allEvents.addAll(events ?: emptyList())
        }

        return allEvents
    }

    @Transactional(readOnly = true)
    fun getCalendarEventsWithDday(memberId: Long?): MutableList<BaseCalendarEventResponse?> {
        val dDayEvents: MutableList<BaseCalendarEventResponse?> = ArrayList<BaseCalendarEventResponse?>()

        // 모든 이벤트 타입에 대해 조회
        for (eventType in CalendarEventType.entries) {
            val calendarEventService = calendarEventFactory.getCalendarEventService(eventType)
            val events = calendarEventService.getCalendarEventsWithDday(memberId)
            dDayEvents.addAll(events ?: emptyList())
        }

        return dDayEvents
    }
}
