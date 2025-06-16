package com.memory.service.calendar.factory;

import com.memory.dto.calendar.CalendarEventRequest;
import com.memory.dto.calendar.response.BaseCalendarEventResponse;

public interface CalendarEventFactoryService {

    BaseCalendarEventResponse createCalendarEvent(Long memberId, CalendarEventRequest.Create request);

}
