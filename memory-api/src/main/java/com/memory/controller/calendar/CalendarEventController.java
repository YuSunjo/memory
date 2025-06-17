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
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

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

    @ApiOperations.SecuredApi(
        summary = "일정 수정",
        description = "기존 일정을 수정합니다. 이벤트 타입에 따라 개인 일정, 관계 일정, 기념일을 수정할 수 있습니다.",
        response = BaseCalendarEventResponse.class
    )
    @Auth
    @PutMapping("api/v1/calendar/events/{eventId}")
    public ServerResponse<BaseCalendarEventResponse> updateCalendarEvent(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long eventId,
            @RequestBody @Valid CalendarEventRequest.Update request) {
        return ServerResponse.success(calendarEventService.updateCalendarEvent(memberId, eventId, request));
    }

    @ApiOperations.SecuredApi(
        summary = "일정 조회",
        description = "특정 기간 내의 일정을 조회합니다. 시작일과 종료일을 기준으로 모든 타입의 일정을 조회합니다.",
        response = BaseCalendarEventResponse.class
    )
    @Auth
    @GetMapping("api/v1/calendar/events")
    public ServerResponse<List<BaseCalendarEventResponse>> getCalendarEventsByDateRange(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ServerResponse.success(calendarEventService.getCalendarEventsByDateRange(memberId, startDate, endDate));
    }
}
