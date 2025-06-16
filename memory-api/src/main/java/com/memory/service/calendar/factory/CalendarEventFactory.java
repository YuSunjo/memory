package com.memory.service.calendar.factory;

import com.memory.domain.calendar.*;
import com.memory.exception.customException.ValidationException;
import com.memory.service.calendar.AnniversaryEventService;
import com.memory.service.calendar.PersonalEventService;
import com.memory.service.calendar.RelationshipEventService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CalendarEventFactory {

    private final Map<CalendarEventType, CalendarEventFactoryService> calendarEventServiceMap = new HashMap<>();

    private final PersonalEventService personalEventService;
    private final RelationshipEventService relationshipEventService;
    private final AnniversaryEventService anniversaryEventService;

    @PostConstruct
    public void init() {
        calendarEventServiceMap.put(CalendarEventType.PERSONAL, personalEventService);
        calendarEventServiceMap.put(CalendarEventType.RELATIONSHIP_EVENT, relationshipEventService);
        calendarEventServiceMap.put(CalendarEventType.ANNIVERSARY_EVENT, anniversaryEventService);
    }

    public CalendarEventFactoryService getCalendarEventService(CalendarEventType calendarEventType) {
        CalendarEventFactoryService service = calendarEventServiceMap.get(calendarEventType);
        if (service == null) {
            throw new ValidationException("지원하지 않는 캘린더 이벤트 타입입니다: " + calendarEventType);
        }
        return service;
    }

}
