package com.memory.service.calendar;

import com.memory.domain.calendar.RelationshipEvent;
import com.memory.domain.calendar.repository.RelationshipEventRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.relationship.Relationship;
import com.memory.domain.relationship.RelationshipStatus;
import com.memory.domain.relationship.repository.RelationshipRepository;
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

import static com.memory.service.calendar.CalendarEventServiceUtils.validateRelationshipMember;

@Service
@RequiredArgsConstructor
public class RelationshipEventService implements CalendarEventFactoryService {

    private final MemberRepository memberRepository;
    private final RelationshipRepository relationshipRepository;
    private final RelationshipEventRepository relationshipEventRepository;

    @Override
    @Transactional
    public BaseCalendarEventResponse createCalendarEvent(Long memberId, CalendarEventRequest.Create request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Relationship> relationshipList = relationshipRepository.findByMemberOrRelatedMemberAndStatus(member, RelationshipStatus.ACCEPTED);
        if (relationshipList.isEmpty()) {
            throw new NotFoundException("회원의 관계가 존재하지 않습니다.");
        }

        // 모든 관계에 대해 관계 이벤트 생성
        List<RelationshipEvent> savedEventList = relationshipList.stream()
                .map(relationship -> {
                    validateRelationshipMember(relationship.getMember(), relationship);
                    RelationshipEvent relationshipEvent = request.toRelationshipEvent(relationship.getMember(), relationship);
                    return relationshipEventRepository.save(relationshipEvent);
                })
                .toList();

        return BaseCalendarEventResponse.from(savedEventList.get(0));
    }

    @Override
    @Transactional
    public BaseCalendarEventResponse updateCalendarEvent(Long memberId, Long eventId, CalendarEventRequest.Update request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        RelationshipEvent relationshipEvent = relationshipEventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("일정을 찾을 수 없습니다."));

        // 접근 권한 확인
        relationshipEvent.validateAccessPermission(member);

        // 일정 업데이트
        relationshipEvent.update(
                request.getTitle(),
                request.getDescription(),
                request.getStartDateTime(),
                request.getEndDateTime(),
                request.getLocation()
        );

        return BaseCalendarEventResponse.from(relationshipEvent);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BaseCalendarEventResponse> getCalendarEventsByDateRange(Long memberId, LocalDateTime startDate, LocalDateTime endDate) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<RelationshipEvent> events = relationshipEventRepository.findByMemberAndStartDateTimeBetween(
                member, startDate, endDate);

        return events.stream()
                .map(BaseCalendarEventResponse::from)
                .collect(Collectors.toList());
    }
}
