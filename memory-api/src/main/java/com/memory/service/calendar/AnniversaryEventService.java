package com.memory.service.calendar;

import com.memory.domain.calendar.AnniversaryEvent;
import com.memory.domain.calendar.repository.AnniversaryEventRepository;
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

import java.util.List;

import static com.memory.service.calendar.CalendarEventServiceUtils.validateRelationshipMember;

@Service
@RequiredArgsConstructor
public class AnniversaryEventService implements CalendarEventFactoryService {

    private final MemberRepository memberRepository;
    private final RelationshipRepository relationshipRepository;
    private final AnniversaryEventRepository anniversaryEventRepository;

    @Override
    @Transactional
    public BaseCalendarEventResponse createCalendarEvent(Long memberId, CalendarEventRequest.Create request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Relationship> relationshipList = relationshipRepository.findByMemberOrRelatedMemberAndStatus(member, RelationshipStatus.ACCEPTED);
        if (relationshipList.isEmpty()) {
            throw new NotFoundException("회원의 관계가 존재하지 않습니다.");
        }

        // 모든 관계에 대해 기념일 이벤트 생성
        List<AnniversaryEvent> savedEventList = relationshipList.stream()
                .map(relationship -> {
                    validateRelationshipMember(relationship.getMember(), relationship);
                    AnniversaryEvent anniversaryEvent = request.toAnniversaryEvent(relationship.getMember(), relationship);
                    return anniversaryEventRepository.save(anniversaryEvent);
                })
                .toList();

        return BaseCalendarEventResponse.from(savedEventList.get(0));
    }
}
