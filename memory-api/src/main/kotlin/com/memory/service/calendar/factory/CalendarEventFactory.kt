package com.memory.service.calendar.factory

import com.memory.domain.calendar.CalendarEventType
import com.memory.exception.customException.ValidationException
import com.memory.service.calendar.AnniversaryEventService
import com.memory.service.calendar.PersonalEventService
import com.memory.service.calendar.RelationshipEventService
import jakarta.annotation.PostConstruct
import lombok.RequiredArgsConstructor
import org.springframework.stereotype.Component

@Component
@RequiredArgsConstructor
class CalendarEventFactory {
    private val calendarEventServiceMap: MutableMap<CalendarEventType?, CalendarEventFactoryService> =
        HashMap()

    private val personalEventService: PersonalEventService? = null
    private val relationshipEventService: RelationshipEventService? = null
    private val anniversaryEventService: AnniversaryEventService? = null

    @PostConstruct
    fun init() {
        calendarEventServiceMap.put(CalendarEventType.PERSONAL, personalEventService!!)
        calendarEventServiceMap.put(CalendarEventType.RELATIONSHIP_EVENT, relationshipEventService!!)
        calendarEventServiceMap.put(CalendarEventType.ANNIVERSARY_EVENT, anniversaryEventService!!)
    }

    fun getCalendarEventService(calendarEventType: CalendarEventType?): CalendarEventFactoryService {
        val service: CalendarEventFactoryService = calendarEventServiceMap[calendarEventType]
            ?: throw ValidationException("지원하지 않는 캘린더 이벤트 타입입니다: $calendarEventType")
        return service
    }
}
