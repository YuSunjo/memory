package com.memory.service.calendar;

import com.memory.dto.calendar.CalendarEventRequest;
import com.memory.dto.calendar.response.BaseCalendarEventResponse;
import com.memory.service.calendar.factory.CalendarEventFactory;
import com.memory.service.calendar.factory.CalendarEventFactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CalendarEventService {

    private final CalendarEventFactory calendarEventFactory;

    /**
     * 통합 일정 생성 API
     * 이벤트 타입에 따라 적절한 일정을 생성합니다.
     */
    @Transactional
    public BaseCalendarEventResponse createCalendarEvent(Long memberId, CalendarEventRequest.Create request) {
        CalendarEventFactoryService calendarEventService = calendarEventFactory.getCalendarEventService(request.getEventType());
        return calendarEventService.createCalendarEvent(memberId, request);
    }
}
