package com.memory.service.calendar;

import com.memory.domain.calendar.CalendarEventType;
import com.memory.dto.calendar.CalendarEventRequest;
import com.memory.dto.calendar.response.BaseCalendarEventResponse;
import com.memory.service.calendar.factory.CalendarEventFactory;
import com.memory.service.calendar.factory.CalendarEventFactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CalendarEventService {

    private final CalendarEventFactory calendarEventFactory;

    @Transactional
    public BaseCalendarEventResponse createCalendarEvent(Long memberId, CalendarEventRequest.Create request) {
        CalendarEventFactoryService calendarEventService = calendarEventFactory.getCalendarEventService(request.getEventType());
        return calendarEventService.createCalendarEvent(memberId, request);
    }

    @Transactional
    public BaseCalendarEventResponse updateCalendarEvent(Long memberId, Long eventId, CalendarEventRequest.Update request) {
        CalendarEventFactoryService calendarEventService = calendarEventFactory.getCalendarEventService(request.getEventType());
        return calendarEventService.updateCalendarEvent(memberId, eventId, request);
    }

    @Transactional(readOnly = true)
    public List<BaseCalendarEventResponse> getCalendarEventsByDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        // LocalDate를 LocalDateTime으로 변환 (시작일은 00:00:00, 종료일은 23:59:59)
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<BaseCalendarEventResponse> allEvents = new ArrayList<>();

        // 모든 이벤트 타입에 대해 조회
        for (CalendarEventType eventType : CalendarEventType.values()) {
            CalendarEventFactoryService calendarEventService = calendarEventFactory.getCalendarEventService(eventType);
            List<BaseCalendarEventResponse> events = calendarEventService.getCalendarEventsByDateRange(memberId, startDateTime, endDateTime);
            allEvents.addAll(events);
        }

        return allEvents;
    }

    @Transactional(readOnly = true)
    public List<BaseCalendarEventResponse> getCalendarEventsWithDday(Long memberId) {
        List<BaseCalendarEventResponse> dDayEvents = new ArrayList<>();

        // 모든 이벤트 타입에 대해 조회
        for (CalendarEventType eventType : CalendarEventType.values()) {
            CalendarEventFactoryService calendarEventService = calendarEventFactory.getCalendarEventService(eventType);
            List<BaseCalendarEventResponse> events = calendarEventService.getCalendarEventsWithDday(memberId);
            dDayEvents.addAll(events);
        }

        return dDayEvents;
    }
}
