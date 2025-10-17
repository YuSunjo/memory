package com.memory.service.calendar.factory

import com.memory.domain.calendar.CalendarEventType
import com.memory.exception.customException.ValidationException
import com.memory.service.calendar.AnniversaryEventService
import com.memory.service.calendar.PersonalEventService
import com.memory.service.calendar.RelationshipEventService
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Component

@Component
class CalendarEventFactory(
    private val personalEventService: PersonalEventService,
    private val relationshipEventService: RelationshipEventService,
    private val anniversaryEventService: AnniversaryEventService
) {
    private val calendarEventServiceMap: MutableMap<CalendarEventType, CalendarEventFactoryService> =
        HashMap()

    @PostConstruct
    fun init() {
        calendarEventServiceMap[CalendarEventType.PERSONAL] = personalEventService
        calendarEventServiceMap[CalendarEventType.RELATIONSHIP_EVENT] = relationshipEventService
        calendarEventServiceMap[CalendarEventType.ANNIVERSARY_EVENT] = anniversaryEventService
    }

    fun getCalendarEventService(calendarEventType: CalendarEventType): CalendarEventFactoryService {
        return calendarEventServiceMap[calendarEventType]
            ?: throw ValidationException("지원하지 않는 캘린더 이벤트 타입입니다: $calendarEventType")
    }
}
