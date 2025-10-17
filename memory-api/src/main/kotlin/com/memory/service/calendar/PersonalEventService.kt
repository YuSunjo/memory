package com.memory.service.calendar

import com.memory.domain.calendar.PersonalEvent
import com.memory.domain.calendar.repository.PersonalEventRepository
import com.memory.domain.member.repository.MemberRepository
import com.memory.dto.calendar.CalendarEventRequest
import com.memory.dto.calendar.response.BaseCalendarEventResponse
import com.memory.exception.customException.NotFoundException
import com.memory.service.calendar.factory.CalendarEventFactoryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.stream.Collectors

@Service
class PersonalEventService(
    private val memberRepository: MemberRepository,
    private val personalEventRepository: PersonalEventRepository,
) : CalendarEventFactoryService {

    @Transactional
    override fun createCalendarEvent(
        memberId: Long?,
        request: CalendarEventRequest.Create
    ): BaseCalendarEventResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val personalEvent = request.toPersonalEvent(member)
        val savedEvent = personalEventRepository.save(personalEvent)

        return BaseCalendarEventResponse.from(savedEvent)
    }

    @Transactional
    override fun updateCalendarEvent(
        memberId: Long?,
        eventId: Long,
        request: CalendarEventRequest.Update
    ): BaseCalendarEventResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val personalEvent = personalEventRepository.findById(eventId)
            .orElseThrow { NotFoundException("일정을 찾을 수 없습니다.") }

        // 접근 권한 확인
        personalEvent.validateAccessPermission(member)

        // 일정 업데이트
        personalEvent.update(
            request.title,
            request.description,
            request.startDateTime,
            request.endDateTime,
            request.location
        )

        return BaseCalendarEventResponse.from(personalEvent)
    }

    @Transactional(readOnly = true)
    override fun getCalendarEventsByDateRange(
        memberId: Long?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): MutableList<BaseCalendarEventResponse?> {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val events: List<PersonalEvent?> = personalEventRepository.findByMemberAndStartDateTimeBetween(
            member, startDate, endDate
        )

        return events.stream()
            .map<BaseCalendarEventResponse?> { event: PersonalEvent? -> BaseCalendarEventResponse.from(event) }
            .collect(Collectors.toList())
    }

    override fun getCalendarEventsWithDday(memberId: Long?): MutableList<BaseCalendarEventResponse?> {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val events: List<PersonalEvent?> = personalEventRepository.findByMemberAndFutureEvents(member)

        return events.stream()
            .map { event: PersonalEvent? -> BaseCalendarEventResponse.from(event) }
            .collect(Collectors.toList())
    }
}
