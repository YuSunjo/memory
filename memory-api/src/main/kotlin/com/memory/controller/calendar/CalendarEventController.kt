package com.memory.controller.calendar

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.calendar.CalendarEventRequest
import com.memory.dto.calendar.response.BaseCalendarEventResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.calendar.CalendarEventService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@Tag(name = "Calendar", description = "Calendar API")
class CalendarEventController(
    private val calendarEventService: CalendarEventService,
) {
    @SecuredApi(
        summary = "일정 생성",
        description = "새로운 일정을 생성합니다. 이벤트 타입에 따라 개인 일정, 관계 일정, 기념일을 생성할 수 있습니다.",
        response = BaseCalendarEventResponse::class
    )
    @Auth
    @PostMapping("api/v1/calendar/events")
    fun createCalendarEvent(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody request: @Valid CalendarEventRequest.Create
    ): ServerResponse<BaseCalendarEventResponse?> {
        return success(calendarEventService.createCalendarEvent(memberId, request))
    }

    @SecuredApi(
        summary = "일정 수정",
        description = "기존 일정을 수정합니다. 이벤트 타입에 따라 개인 일정, 관계 일정, 기념일을 수정할 수 있습니다.",
        response = BaseCalendarEventResponse::class
    )
    @Auth
    @PutMapping("api/v1/calendar/events/{eventId}")
    fun updateCalendarEvent(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable eventId: Long,
        @RequestBody request: @Valid CalendarEventRequest.Update
    ): ServerResponse<BaseCalendarEventResponse?> {
        return success(
            calendarEventService.updateCalendarEvent(
                memberId,
                eventId,
                request
            )
        )
    }

    @SecuredApi(
        summary = "일정 조회",
        description = "특정 기간 내의 일정을 조회합니다. 시작일과 종료일을 기준으로 모든 타입의 일정을 조회합니다.",
        response = BaseCalendarEventResponse::class
    )
    @Auth
    @GetMapping("api/v1/calendar/events")
    fun getCalendarEventsByDateRange(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ServerResponse<MutableList<BaseCalendarEventResponse?>?> {
        return success(
            calendarEventService.getCalendarEventsByDateRange(
                memberId,
                startDate,
                endDate
            )
        )
    }

    @SecuredApi(
        summary = "D-day 조회",
        description = "D-day 정보가 있는 일정을 조회합니다. AnniversaryEvent는 모든 경우에, PersonalEvent와 RelationshipEvent는 미래 날짜인 경우에만 D-day 정보가 포함됩니다.",
        response = BaseCalendarEventResponse::class
    )
    @Auth
    @GetMapping("api/v1/calendar/events/dday")
    fun getCalendarEventsWithDday(
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<MutableList<BaseCalendarEventResponse?>?> {
        return success(
            calendarEventService.getCalendarEventsWithDday(
                memberId
            )
        )
    }
}
