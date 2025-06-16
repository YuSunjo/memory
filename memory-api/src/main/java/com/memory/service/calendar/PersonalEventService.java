package com.memory.service.calendar;

import com.memory.domain.calendar.repository.PersonalEventRepository;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.calendar.CalendarEventRequest;
import com.memory.dto.calendar.response.BaseCalendarEventResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.service.calendar.factory.CalendarEventFactoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PersonalEventService implements CalendarEventFactoryService {

    private final MemberRepository memberRepository;
    private final PersonalEventRepository personalEventRepository;

    @Override
    public BaseCalendarEventResponse createCalendarEvent(Long memberId, CalendarEventRequest.Create request) {
        var member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        var personalEvent = request.toPersonalEvent(member);
        var savedEvent = personalEventRepository.save(personalEvent);

        return BaseCalendarEventResponse.from(savedEvent);
    }
}
