package com.memory.service.calendar;

import com.memory.domain.calendar.PersonalEvent;
import com.memory.domain.calendar.repository.PersonalEventRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.calendar.CalendarEventRequest;
import com.memory.dto.calendar.response.BaseCalendarEventResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.service.calendar.factory.CalendarEventFactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonalEventService implements CalendarEventFactoryService {

    private final MemberRepository memberRepository;
    private final PersonalEventRepository personalEventRepository;

    @Override
    @Transactional
    public BaseCalendarEventResponse createCalendarEvent(Long memberId, CalendarEventRequest.Create request) {
        var member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        var personalEvent = request.toPersonalEvent(member);
        var savedEvent = personalEventRepository.save(personalEvent);

        return BaseCalendarEventResponse.from(savedEvent);
    }

    @Override
    @Transactional
    public BaseCalendarEventResponse updateCalendarEvent(Long memberId, Long eventId, CalendarEventRequest.Update request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        PersonalEvent personalEvent = personalEventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));

        // 접근 권한 확인
        personalEvent.validateAccessPermission(member);

        // 일정 업데이트
        personalEvent.update(
                request.getTitle(),
                request.getDescription(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getLocation()
        );

        return BaseCalendarEventResponse.from(personalEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaseCalendarEventResponse> getCalendarEventsByDateRange(Long memberId, LocalDateTime startDate, LocalDateTime endDate) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<PersonalEvent> events = personalEventRepository.findByMemberAndStartDateTimeBetween(
                member, startDate, endDate);

        return events.stream()
                .map(BaseCalendarEventResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<BaseCalendarEventResponse> getCalendarEventsWithDday(Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<PersonalEvent> events = personalEventRepository.findByMemberAndFutureEvents(member);

        return events.stream()
                .map(BaseCalendarEventResponse::from)
                .collect(Collectors.toList());
    }
}
