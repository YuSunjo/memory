package com.memory.controller.calendar;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.calendar.CalendarEventRequest;
import com.memory.dto.calendar.response.BaseCalendarEventResponse;
import com.memory.response.ServerResponse;
import com.memory.service.calendar.CalendarEventService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Tag(name = "Calendar", description = "Calendar API")
public class CalendarEventController {

    private final CalendarEventService calendarEventService;

    @ApiOperations.SecuredApi(
        summary = "일정 생성",
        description = "새로운 일정을 생성합니다. 이벤트 타입에 따라 개인 일정, 관계 일정, 기념일을 생성할 수 있습니다.",
        response = BaseCalendarEventResponse.class
    )
    @Auth
    @PostMapping("api/v1/calendar/events")
    public ServerResponse<BaseCalendarEventResponse> createCalendarEvent(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody @Valid CalendarEventRequest.Create request) {
        return ServerResponse.success(calendarEventService.createCalendarEvent(memberId, request));
    }

}
