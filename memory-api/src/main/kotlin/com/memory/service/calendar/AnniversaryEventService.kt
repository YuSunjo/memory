package com.memory.service.calendar

import com.memory.domain.calendar.AnniversaryEvent
import com.memory.domain.calendar.repository.AnniversaryEventRepository
import com.memory.domain.member.repository.MemberRepository
import com.memory.domain.relationship.Relationship
import com.memory.domain.relationship.RelationshipStatus
import com.memory.domain.relationship.repository.RelationshipRepository
import com.memory.dto.calendar.CalendarEventRequest
import com.memory.dto.calendar.response.BaseCalendarEventResponse
import com.memory.exception.customException.NotFoundException
import com.memory.service.calendar.factory.CalendarEventFactoryService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.util.stream.Collectors

@Service
class AnniversaryEventService(
    private val memberRepository: MemberRepository,
    private val relationshipRepository: RelationshipRepository,
    private val anniversaryEventRepository: AnniversaryEventRepository,
) : CalendarEventFactoryService {

    @Transactional
    override fun createCalendarEvent(
        memberId: Long?,
        request: CalendarEventRequest.Create
    ): BaseCalendarEventResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val relationshipList: List<Relationship?> =
            relationshipRepository.findByMemberOrRelatedMemberAndStatus(member, RelationshipStatus.ACCEPTED)
        if (relationshipList.isEmpty()) {
            throw NotFoundException("회원의 관계가 존재하지 않습니다.")
        }

        // 모든 관계에 대해 기념일 이벤트 생성
        val savedEventList = relationshipList.stream()
            .map { relationship: Relationship? ->
                relationship?.let { rel ->
                    CalendarEventServiceUtils.validateRelationshipMember(rel.member, rel)
                    val anniversaryEvent = request.toAnniversaryEvent(rel.member, rel)
                    anniversaryEventRepository.save(anniversaryEvent)
                }
            }
            .filter { it != null }
            .map { it!! }
            .toList()

        return BaseCalendarEventResponse.from(savedEventList.get(0))
    }

    @Transactional
    override fun updateCalendarEvent(
        memberId: Long?,
        eventId: Long,
        request: CalendarEventRequest.Update
    ): BaseCalendarEventResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val anniversaryEvent = anniversaryEventRepository.findById(eventId)
            .orElseThrow { NotFoundException("일정을 찾을 수 없습니다.") }

        // 접근 권한 확인
        anniversaryEvent.validateAccessPermission(member)

        // 일정 업데이트
        anniversaryEvent.update(
            request.title,
            request.description,
            request.startDateTime,
            request.endDateTime,
            request.location,
            request.isDday ?: false
        )

        return BaseCalendarEventResponse.from(anniversaryEvent)
    }

    @Transactional(readOnly = true)
    override fun getCalendarEventsByDateRange(
        memberId: Long?,
        startDate: LocalDateTime?,
        endDate: LocalDateTime?
    ): MutableList<BaseCalendarEventResponse?> {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val events: List<AnniversaryEvent?> =
            anniversaryEventRepository.findByMemberAndStartDateTimeBetween(member, startDate, endDate)

        return events.stream()
            .map<BaseCalendarEventResponse?> { event: AnniversaryEvent? -> BaseCalendarEventResponse.from(event) }
            .collect(Collectors.toList())
    }

    override fun getCalendarEventsWithDday(memberId: Long?): MutableList<BaseCalendarEventResponse?> {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val relationshipList: List<Relationship?> =
            relationshipRepository.findByMemberOrRelatedMemberAndStatus(member, RelationshipStatus.ACCEPTED)
        if (relationshipList.isEmpty()) {
            return mutableListOf<BaseCalendarEventResponse?>()
        }

        val events: List<AnniversaryEvent?> = anniversaryEventRepository.findByMemberAndIsDdayTrue(member)

        return events.stream()
            .map<BaseCalendarEventResponse?> { event: AnniversaryEvent? -> BaseCalendarEventResponse.from(event) }
            .collect(Collectors.toList())
    }
}
