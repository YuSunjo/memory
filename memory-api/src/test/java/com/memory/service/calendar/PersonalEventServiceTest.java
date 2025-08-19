package com.memory.service.calendar;

import com.memory.domain.calendar.PersonalEvent;
import com.memory.domain.calendar.repository.PersonalEventRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.calendar.CalendarEventRequest;
import com.memory.dto.calendar.response.BaseCalendarEventResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonalEventServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private PersonalEventRepository personalEventRepository;

    @InjectMocks
    private PersonalEventService personalEventService;

    private Member member;
    private Member otherMember;
    private PersonalEvent personalEvent;
    private CalendarEventRequest.Create createRequest;
    private CalendarEventRequest.Update updateRequest;

    private final Long memberId = 1L;
    private final Long otherMemberId = 2L;
    private final Long eventId = 1L;
    private final String title = "개인 일정";
    private final String description = "개인 일정 설명";
    private final LocalDateTime startDateTime = LocalDateTime.of(2024, 8, 19, 10, 0);
    private final LocalDateTime endDateTime = LocalDateTime.of(2024, 8, 19, 12, 0);
    private final String location = "테스트 장소";

    @BeforeEach
    void setUp() {
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        otherMember = new Member("다른 사용자", "otheruser", "other@example.com", "encodedPassword");
        setId(otherMember, otherMemberId);

        personalEvent = PersonalEvent.create(title, description, startDateTime, endDateTime, location, member);
        setId(personalEvent, eventId);

        createRequest = new CalendarEventRequest.Create(
                title,
                description,
                startDateTime,
                endDateTime,
                location,
                null,
                false
        );

        updateRequest = new CalendarEventRequest.Update(
                "수정된 제목",
                "수정된 설명",
                startDateTime.plusHours(1),
                endDateTime.plusHours(1),
                "수정된 장소",
                null,
                false
        );
    }

    private void setId(Object entity, Long id) {
        try {
            Field idField = null;
            Class<?> clazz = entity.getClass();
            
            // 상위 클래스까지 탐색하여 id 필드 찾기
            while (clazz != null && idField == null) {
                try {
                    idField = clazz.getDeclaredField("id");
                } catch (NoSuchFieldException e) {
                    clazz = clazz.getSuperclass();
                }
            }
            
            if (idField == null) {
                throw new NoSuchFieldException("id field not found in class hierarchy");
            }
            
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @Test
    @DisplayName("개인 일정 생성 성공 테스트")
    void createPersonalEventSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(personalEventRepository.save(any(PersonalEvent.class))).thenReturn(personalEvent);

        // When
        BaseCalendarEventResponse response = personalEventService.createCalendarEvent(memberId, createRequest);

        // Then
        assertNotNull(response);

        verify(memberRepository).findMemberById(memberId);
        verify(personalEventRepository).save(any(PersonalEvent.class));
    }

    @Test
    @DisplayName("개인 일정 생성 실패 테스트 - 존재하지 않는 회원")
    void createPersonalEventFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> personalEventService.createCalendarEvent(memberId, createRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(personalEventRepository, never()).save(any());
    }

    @Test
    @DisplayName("개인 일정 수정 성공 테스트")
    void updatePersonalEventSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(personalEventRepository.findById(eventId)).thenReturn(Optional.of(personalEvent));

        // When
        BaseCalendarEventResponse response = personalEventService.updateCalendarEvent(memberId, eventId, updateRequest);

        // Then
        assertNotNull(response);
        
        verify(memberRepository).findMemberById(memberId);
        verify(personalEventRepository).findById(eventId);
        
        // 실제 객체이므로 업데이트된 상태 확인
        assertEquals(updateRequest.getTitle(), personalEvent.getTitle());
        assertEquals(updateRequest.getDescription(), personalEvent.getDescription());
        assertEquals(updateRequest.getStartDateTime(), personalEvent.getStartDateTime());
        assertEquals(updateRequest.getEndDateTime(), personalEvent.getEndDateTime());
        assertEquals(updateRequest.getLocation(), personalEvent.getLocation());
    }

    @Test
    @DisplayName("개인 일정 수정 실패 테스트 - 존재하지 않는 회원")
    void updatePersonalEventFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> personalEventService.updateCalendarEvent(memberId, eventId, updateRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(personalEventRepository, never()).findById(any());
    }

    @Test
    @DisplayName("개인 일정 수정 실패 테스트 - 존재하지 않는 일정")
    void updatePersonalEventFailEventNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(personalEventRepository.findById(eventId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> personalEventService.updateCalendarEvent(memberId, eventId, updateRequest));

        assertEquals("일정을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(personalEventRepository).findById(eventId);
    }

    @Test
    @DisplayName("개인 일정 수정 실패 테스트 - 접근 권한 없음")
    void updatePersonalEventFailNoPermission() {
        // Given - 다른 사용자의 일정 생성 (실제 객체 사용)
        PersonalEvent otherPersonalEvent = PersonalEvent.create(title, description, startDateTime, endDateTime, location, otherMember);
        setId(otherPersonalEvent, eventId);
        
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(personalEventRepository.findById(eventId)).thenReturn(Optional.of(otherPersonalEvent));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> personalEventService.updateCalendarEvent(memberId, eventId, updateRequest));

        assertEquals("이 일정에 접근할 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("날짜 범위로 개인 일정 조회 성공 테스트")
    void getPersonalEventsByDateRangeSuccess() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 8, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 8, 31, 23, 59);

        PersonalEvent event1 = PersonalEvent.create("이벤트1", "설명1", startDate, startDate.plusHours(2), "장소1", member);
        PersonalEvent event2 = PersonalEvent.create("이벤트2", "설명2", startDate.plusDays(1), startDate.plusDays(1).plusHours(2), "장소2", member);
        setId(event1, 10L);
        setId(event2, 11L);
        List<PersonalEvent> events = Arrays.asList(event1, event2);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(personalEventRepository.findByMemberAndStartDateTimeBetween(member, startDate, endDate))
                .thenReturn(events);

        // When
        List<BaseCalendarEventResponse> responses = personalEventService.getCalendarEventsByDateRange(memberId, startDate, endDate);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        verify(memberRepository).findMemberById(memberId);
        verify(personalEventRepository).findByMemberAndStartDateTimeBetween(member, startDate, endDate);
    }

    @Test
    @DisplayName("날짜 범위로 개인 일정 조회 실패 테스트 - 존재하지 않는 회원")
    void getPersonalEventsByDateRangeFailMemberNotFound() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 8, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 8, 31, 23, 59);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> personalEventService.getCalendarEventsByDateRange(memberId, startDate, endDate));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(personalEventRepository, never()).findByMemberAndStartDateTimeBetween(any(), any(), any());
    }

    @Test
    @DisplayName("D-day 개인 일정 조회 성공 테스트")
    void getPersonalEventsWithDdaySuccess() {
        // Given
        LocalDateTime futureDate = LocalDateTime.now().plusDays(7);
        PersonalEvent futureEvent1 = PersonalEvent.create("미래 이벤트1", "미래 설명1", futureDate, futureDate.plusHours(2), "미래 장소1", member);
        PersonalEvent futureEvent2 = PersonalEvent.create("미래 이벤트2", "미래 설명2", futureDate.plusDays(1), futureDate.plusDays(1).plusHours(2), "미래 장소2", member);
        setId(futureEvent1, 20L);
        setId(futureEvent2, 21L);
        List<PersonalEvent> futureEvents = Arrays.asList(futureEvent1, futureEvent2);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(personalEventRepository.findByMemberAndFutureEvents(member)).thenReturn(futureEvents);

        // When
        List<BaseCalendarEventResponse> responses = personalEventService.getCalendarEventsWithDday(memberId);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        verify(memberRepository).findMemberById(memberId);
        verify(personalEventRepository).findByMemberAndFutureEvents(member);
    }

    @Test
    @DisplayName("D-day 개인 일정 조회 실패 테스트 - 존재하지 않는 회원")
    void getPersonalEventsWithDdayFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> personalEventService.getCalendarEventsWithDday(memberId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(personalEventRepository, never()).findByMemberAndFutureEvents(any());
    }

    @Test
    @DisplayName("빈 결과 처리 테스트")
    void emptyResultHandlingTest() {
        // Given
        LocalDateTime startDate = LocalDateTime.of(2024, 8, 1, 0, 0);
        LocalDateTime endDate = LocalDateTime.of(2024, 8, 31, 23, 59);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(personalEventRepository.findByMemberAndStartDateTimeBetween(member, startDate, endDate))
                .thenReturn(List.of());
        when(personalEventRepository.findByMemberAndFutureEvents(member))
                .thenReturn(List.of());

        // When
        List<BaseCalendarEventResponse> dateRangeResponses = personalEventService.getCalendarEventsByDateRange(memberId, startDate, endDate);
        List<BaseCalendarEventResponse> ddayResponses = personalEventService.getCalendarEventsWithDday(memberId);

        // Then
        assertNotNull(dateRangeResponses);
        assertNotNull(ddayResponses);
        assertTrue(dateRangeResponses.isEmpty());
        assertTrue(ddayResponses.isEmpty());
    }

    @Test
    @DisplayName("개인 일정 생성 시 요청 객체 변환 테스트")
    void createPersonalEventRequestConversionTest() {
        // Given
        CalendarEventRequest.Create specificRequest = new CalendarEventRequest.Create(
                "특별한 일정",
                "중요한 개인 일정",
                startDateTime.plusDays(1),
                endDateTime.plusDays(1),
                "특별한 장소",
                null,
                true
        );

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(personalEventRepository.save(any(PersonalEvent.class))).thenReturn(personalEvent);

        // When
        BaseCalendarEventResponse response = personalEventService.createCalendarEvent(memberId, specificRequest);

        // Then
        assertNotNull(response);
        verify(personalEventRepository).save(any(PersonalEvent.class));
    }

    @Test
    @DisplayName("개인 일정 수정 시 모든 필드 업데이트 확인 테스트")
    void updatePersonalEventAllFieldsTest() {
        // Given
        CalendarEventRequest.Update completeUpdateRequest = new CalendarEventRequest.Update(
                "완전히 새로운 제목",
                "완전히 새로운 설명",
                startDateTime.plusDays(2),
                endDateTime.plusDays(2),
                "완전히 새로운 장소",
                null,
                false
        );

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(personalEventRepository.findById(eventId)).thenReturn(Optional.of(personalEvent));

        // When
        BaseCalendarEventResponse response = personalEventService.updateCalendarEvent(memberId, eventId, completeUpdateRequest);

        // Then
        assertNotNull(response);
        
        // 실제 객체이므로 업데이트된 상태 확인
        assertEquals("완전히 새로운 제목", personalEvent.getTitle());
        assertEquals("완전히 새로운 설명", personalEvent.getDescription());
        assertEquals(startDateTime.plusDays(2), personalEvent.getStartDateTime());
        assertEquals(endDateTime.plusDays(2), personalEvent.getEndDateTime());
        assertEquals("완전히 새로운 장소", personalEvent.getLocation());
    }
}