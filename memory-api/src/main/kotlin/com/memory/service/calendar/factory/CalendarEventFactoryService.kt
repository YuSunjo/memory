package com.memory.service.calendar.factory;

import com.memory.dto.calendar.CalendarEventRequest;
import com.memory.dto.calendar.response.BaseCalendarEventResponse;

import java.util.List;
import java.time.LocalDateTime;

public interface CalendarEventFactoryService {

    BaseCalendarEventResponse createCalendarEvent(Long memberId, CalendarEventRequest.Create request);

    BaseCalendarEventResponse updateCalendarEvent(Long memberId, Long eventId, CalendarEventRequest.Update request);

    List<BaseCalendarEventResponse> getCalendarEventsByDateRange(Long memberId, LocalDateTime startDate, LocalDateTime endDate);

    List<BaseCalendarEventResponse> getCalendarEventsWithDday(Long memberId);
}
