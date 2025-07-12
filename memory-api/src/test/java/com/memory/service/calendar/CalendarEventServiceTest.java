package com.memory.service.calendar;

import com.memory.domain.calendar.CalendarEventType;
import com.memory.dto.calendar.CalendarEventRequest;
import com.memory.dto.calendar.response.AnniversaryEventResponse;
import com.memory.dto.calendar.response.BaseCalendarEventResponse;
import com.memory.dto.calendar.response.PersonalEventResponse;
import com.memory.dto.calendar.response.RelationshipEventResponse;
import com.memory.dto.member.response.MemberResponse;
import com.memory.service.calendar.factory.CalendarEventFactory;
import com.memory.service.calendar.factory.CalendarEventFactoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalendarEventServiceTest {

    @Mock
    private CalendarEventFactory calendarEventFactory;

    @Mock
    private CalendarEventFactoryService personalEventService;

    @Mock
    private CalendarEventFactoryService anniversaryEventService;

    @Mock
    private CalendarEventFactoryService relationshipEventService;

    @InjectMocks
    private CalendarEventService calendarEventService;

    private CalendarEventRequest.Create createRequest;
    private CalendarEventRequest.Update updateRequest;
    private PersonalEventResponse mockPersonalResponse;
    private MemberResponse mockMemberResponse;

    private final Long memberId = 1L;
    private final Long eventId = 1L;
    private final String title = "테스트 이벤트";
    private final String description = "테스트 설명";
    private final LocalDateTime startDateTime = LocalDateTime.of(2025, 7, 15, 10, 0);
    private final LocalDateTime endDateTime = LocalDateTime.of(2025, 7, 15, 12, 0);
    private final String location = "테스트 장소";

    @BeforeEach
    void setUp() {
        // MemberResponse 생성
        mockMemberResponse = new MemberResponse(memberId, "test@example.com", "테스트사용자", 
                "testuser", null, null);

        // Request 객체들 생성
        createRequest = new CalendarEventRequest.Create(title, description, startDateTime, 
                endDateTime, location, CalendarEventType.PERSONAL, false);

        updateRequest = new CalendarEventRequest.Update("수정된 제목", "수정된 설명", 
                startDateTime.plusDays(1), endDateTime.plusDays(1), "수정된 장소", 
                CalendarEventType.PERSONAL, true);

        // Mock Response 객체들 생성
        mockPersonalResponse = new PersonalEventResponse(eventId, title, description,
                startDateTime, endDateTime, location, mockMemberResponse, 
                LocalDateTime.now(), 5);
    }

    @Test
    @DisplayName("개인 일정 생성 성공 테스트")
    void createPersonalEventSuccess() {
        // Given
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.PERSONAL))
                .thenReturn(personalEventService);
        when(personalEventService.createCalendarEvent(memberId, createRequest))
                .thenReturn(mockPersonalResponse);

        // When
        BaseCalendarEventResponse response = calendarEventService.createCalendarEvent(memberId, createRequest);

        // Then
        assertNotNull(response);
        assertEquals(eventId, response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(description, response.getDescription());
        assertEquals(startDateTime, response.getStartDateTime());
        assertEquals(endDateTime, response.getEndDateTime());
        assertEquals(location, response.getLocation());

        verify(calendarEventFactory).getCalendarEventService(CalendarEventType.PERSONAL);
        verify(personalEventService).createCalendarEvent(memberId, createRequest);
    }

    @Test
    @DisplayName("기념일 이벤트 생성 성공 테스트")
    void createAnniversaryEventSuccess() {
        // Given
        CalendarEventRequest.Create anniversaryRequest = new CalendarEventRequest.Create(
                "기념일", "첫 만남 기념일", startDateTime, endDateTime, location, 
                CalendarEventType.ANNIVERSARY_EVENT, true);

        AnniversaryEventResponse anniversaryResponse = new AnniversaryEventResponse(
                2L, "기념일", "첫 만남 기념일", startDateTime, endDateTime, location, 
                mockMemberResponse, LocalDateTime.now(), mockMemberResponse, true, 15);

        when(calendarEventFactory.getCalendarEventService(CalendarEventType.ANNIVERSARY_EVENT))
                .thenReturn(anniversaryEventService);
        when(anniversaryEventService.createCalendarEvent(memberId, anniversaryRequest))
                .thenReturn(anniversaryResponse);

        // When
        BaseCalendarEventResponse response = calendarEventService.createCalendarEvent(memberId, anniversaryRequest);

        // Then
        assertNotNull(response);
        assertEquals(2L, response.getId());
        assertEquals("기념일", response.getTitle());
        assertTrue(((AnniversaryEventResponse) response).getIsDday());

        verify(calendarEventFactory).getCalendarEventService(CalendarEventType.ANNIVERSARY_EVENT);
        verify(anniversaryEventService).createCalendarEvent(memberId, anniversaryRequest);
    }

    @Test
    @DisplayName("관계 이벤트 생성 성공 테스트")
    void createRelationshipEventSuccess() {
        // Given
        CalendarEventRequest.Create relationshipRequest = new CalendarEventRequest.Create(
                "데이트", "영화 보기", startDateTime, endDateTime, "CGV", 
                CalendarEventType.RELATIONSHIP_EVENT, false);

        RelationshipEventResponse relationshipResponse = new RelationshipEventResponse(
                3L, "데이트", "영화 보기", startDateTime, endDateTime, "CGV", 
                mockMemberResponse, LocalDateTime.now(), mockMemberResponse, 7);

        when(calendarEventFactory.getCalendarEventService(CalendarEventType.RELATIONSHIP_EVENT))
                .thenReturn(relationshipEventService);
        when(relationshipEventService.createCalendarEvent(memberId, relationshipRequest))
                .thenReturn(relationshipResponse);

        // When
        BaseCalendarEventResponse response = calendarEventService.createCalendarEvent(memberId, relationshipRequest);

        // Then
        assertNotNull(response);
        assertEquals(3L, response.getId());
        assertEquals("데이트", response.getTitle());

        verify(calendarEventFactory).getCalendarEventService(CalendarEventType.RELATIONSHIP_EVENT);
        verify(relationshipEventService).createCalendarEvent(memberId, relationshipRequest);
    }

    @Test
    @DisplayName("일정 수정 성공 테스트")
    void updateCalendarEventSuccess() {
        // Given
        PersonalEventResponse updatedResponse = new PersonalEventResponse(
                eventId, "수정된 제목", "수정된 설명", startDateTime.plusDays(1), 
                endDateTime.plusDays(1), "수정된 장소", mockMemberResponse, 
                LocalDateTime.now(), 4);

        when(calendarEventFactory.getCalendarEventService(CalendarEventType.PERSONAL))
                .thenReturn(personalEventService);
        when(personalEventService.updateCalendarEvent(memberId, eventId, updateRequest))
                .thenReturn(updatedResponse);

        // When
        BaseCalendarEventResponse response = calendarEventService.updateCalendarEvent(memberId, eventId, updateRequest);

        // Then
        assertNotNull(response);
        assertEquals(eventId, response.getId());
        assertEquals("수정된 제목", response.getTitle());
        assertEquals("수정된 설명", response.getDescription());
        assertEquals("수정된 장소", response.getLocation());

        verify(calendarEventFactory).getCalendarEventService(CalendarEventType.PERSONAL);
        verify(personalEventService).updateCalendarEvent(memberId, eventId, updateRequest);
    }

    @Test
    @DisplayName("날짜 범위별 일정 조회 성공 테스트")
    void getCalendarEventsByDateRangeSuccess() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        PersonalEventResponse personalEvent = new PersonalEventResponse(
                1L, "개인 일정", "개인 설명", this.startDateTime, this.endDateTime, location, 
                mockMemberResponse, LocalDateTime.now(), 5);

        AnniversaryEventResponse anniversaryEvent = new AnniversaryEventResponse(
                2L, "기념일", "기념일 설명", this.startDateTime.plusDays(1), this.endDateTime.plusDays(1), location, 
                mockMemberResponse, LocalDateTime.now(), mockMemberResponse, true, 10);

        RelationshipEventResponse relationshipEvent = new RelationshipEventResponse(
                3L, "관계 일정", "관계 설명", this.startDateTime.plusDays(2), this.endDateTime.plusDays(2), location, 
                mockMemberResponse, LocalDateTime.now(), mockMemberResponse, 3);

        when(calendarEventFactory.getCalendarEventService(CalendarEventType.PERSONAL))
                .thenReturn(personalEventService);
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.ANNIVERSARY_EVENT))
                .thenReturn(anniversaryEventService);
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.RELATIONSHIP_EVENT))
                .thenReturn(relationshipEventService);

        when(personalEventService.getCalendarEventsByDateRange(memberId, startDateTime, endDateTime))
                .thenReturn(List.of(personalEvent));
        when(anniversaryEventService.getCalendarEventsByDateRange(memberId, startDateTime, endDateTime))
                .thenReturn(List.of(anniversaryEvent));
        when(relationshipEventService.getCalendarEventsByDateRange(memberId, startDateTime, endDateTime))
                .thenReturn(List.of(relationshipEvent));

        // When
        List<BaseCalendarEventResponse> responses = calendarEventService.getCalendarEventsByDateRange(memberId, startDate, endDate);

        // Then
        assertNotNull(responses);
        assertEquals(3, responses.size());
        
        // 각 이벤트 타입별로 하나씩 있는지 확인
        assertTrue(responses.stream().anyMatch(event -> event instanceof PersonalEventResponse));
        assertTrue(responses.stream().anyMatch(event -> event instanceof AnniversaryEventResponse));
        assertTrue(responses.stream().anyMatch(event -> event instanceof RelationshipEventResponse));

        verify(calendarEventFactory, times(3)).getCalendarEventService(any(CalendarEventType.class));
        verify(personalEventService).getCalendarEventsByDateRange(memberId, startDateTime, endDateTime);
        verify(anniversaryEventService).getCalendarEventsByDateRange(memberId, startDateTime, endDateTime);
        verify(relationshipEventService).getCalendarEventsByDateRange(memberId, startDateTime, endDateTime);
    }

    @Test
    @DisplayName("날짜 범위별 일정 조회 테스트 - 빈 결과")
    void getCalendarEventsByDateRangeEmptyResult() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 8, 1);
        LocalDate endDate = LocalDate.of(2025, 8, 31);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        when(calendarEventFactory.getCalendarEventService(CalendarEventType.PERSONAL))
                .thenReturn(personalEventService);
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.ANNIVERSARY_EVENT))
                .thenReturn(anniversaryEventService);
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.RELATIONSHIP_EVENT))
                .thenReturn(relationshipEventService);

        when(personalEventService.getCalendarEventsByDateRange(memberId, startDateTime, endDateTime))
                .thenReturn(List.of());
        when(anniversaryEventService.getCalendarEventsByDateRange(memberId, startDateTime, endDateTime))
                .thenReturn(List.of());
        when(relationshipEventService.getCalendarEventsByDateRange(memberId, startDateTime, endDateTime))
                .thenReturn(List.of());

        // When
        List<BaseCalendarEventResponse> responses = calendarEventService.getCalendarEventsByDateRange(memberId, startDate, endDate);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(calendarEventFactory, times(3)).getCalendarEventService(any(CalendarEventType.class));
        verify(personalEventService).getCalendarEventsByDateRange(memberId, startDateTime, endDateTime);
        verify(anniversaryEventService).getCalendarEventsByDateRange(memberId, startDateTime, endDateTime);
        verify(relationshipEventService).getCalendarEventsByDateRange(memberId, startDateTime, endDateTime);
    }

    @Test
    @DisplayName("D-Day 일정 조회 성공 테스트")
    void getCalendarEventsWithDdaySuccess() {
        // Given
        AnniversaryEventResponse ddayEvent1 = new AnniversaryEventResponse(
                1L, "생일", "생일 기념일", startDateTime, endDateTime, location, 
                mockMemberResponse, LocalDateTime.now(), mockMemberResponse, true, 0);

        PersonalEventResponse ddayEvent2 = new PersonalEventResponse(
                2L, "중요한 약속", "중요한 개인 약속", startDateTime.plusDays(3), endDateTime.plusDays(3), location, 
                mockMemberResponse, LocalDateTime.now(), 3);

        when(calendarEventFactory.getCalendarEventService(CalendarEventType.PERSONAL))
                .thenReturn(personalEventService);
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.ANNIVERSARY_EVENT))
                .thenReturn(anniversaryEventService);
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.RELATIONSHIP_EVENT))
                .thenReturn(relationshipEventService);

        when(personalEventService.getCalendarEventsWithDday(memberId))
                .thenReturn(Collections.singletonList(ddayEvent2));
        when(anniversaryEventService.getCalendarEventsWithDday(memberId))
                .thenReturn(Collections.singletonList(ddayEvent1));
        when(relationshipEventService.getCalendarEventsWithDday(memberId))
                .thenReturn(List.of());

        // When
        List<BaseCalendarEventResponse> responses = calendarEventService.getCalendarEventsWithDday(memberId);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        verify(calendarEventFactory, times(3)).getCalendarEventService(any(CalendarEventType.class));
        verify(personalEventService).getCalendarEventsWithDday(memberId);
        verify(anniversaryEventService).getCalendarEventsWithDday(memberId);
        verify(relationshipEventService).getCalendarEventsWithDday(memberId);
    }

    @Test
    @DisplayName("D-Day 일정 조회 테스트 - 빈 결과")
    void getCalendarEventsWithDdayEmptyResult() {
        // Given
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.PERSONAL))
                .thenReturn(personalEventService);
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.ANNIVERSARY_EVENT))
                .thenReturn(anniversaryEventService);
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.RELATIONSHIP_EVENT))
                .thenReturn(relationshipEventService);

        when(personalEventService.getCalendarEventsWithDday(memberId))
                .thenReturn(List.of());
        when(anniversaryEventService.getCalendarEventsWithDday(memberId))
                .thenReturn(List.of());
        when(relationshipEventService.getCalendarEventsWithDday(memberId))
                .thenReturn(List.of());

        // When
        List<BaseCalendarEventResponse> responses = calendarEventService.getCalendarEventsWithDday(memberId);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(calendarEventFactory, times(3)).getCalendarEventService(any(CalendarEventType.class));
        verify(personalEventService).getCalendarEventsWithDday(memberId);
        verify(anniversaryEventService).getCalendarEventsWithDday(memberId);
        verify(relationshipEventService).getCalendarEventsWithDday(memberId);
    }

    @Test
    @DisplayName("모든 이벤트 타입에 대한 팩토리 호출 확인 테스트")
    void verifyAllEventTypesAreCalled() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);

        when(calendarEventFactory.getCalendarEventService(any(CalendarEventType.class)))
                .thenReturn(personalEventService);
        when(personalEventService.getCalendarEventsByDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        // When
        calendarEventService.getCalendarEventsByDateRange(memberId, startDate, endDate);

        // Then - 모든 이벤트 타입에 대해 팩토리가 호출되는지 확인
        verify(calendarEventFactory).getCalendarEventService(CalendarEventType.PERSONAL);
        verify(calendarEventFactory).getCalendarEventService(CalendarEventType.ANNIVERSARY_EVENT);
        verify(calendarEventFactory).getCalendarEventService(CalendarEventType.RELATIONSHIP_EVENT);
    }

    @Test
    @DisplayName("단일 이벤트 타입만 있는 경우 테스트")
    void getSingleEventTypeOnly() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);
        
        PersonalEventResponse singleEvent = new PersonalEventResponse(
                1L, "단일 이벤트", "설명", startDateTime, endDateTime, location, 
                mockMemberResponse, LocalDateTime.now(), 7);

        when(calendarEventFactory.getCalendarEventService(CalendarEventType.PERSONAL))
                .thenReturn(personalEventService);
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.ANNIVERSARY_EVENT))
                .thenReturn(anniversaryEventService);
        when(calendarEventFactory.getCalendarEventService(CalendarEventType.RELATIONSHIP_EVENT))
                .thenReturn(relationshipEventService);

        when(personalEventService.getCalendarEventsByDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(singleEvent));
        when(anniversaryEventService.getCalendarEventsByDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());
        when(relationshipEventService.getCalendarEventsByDateRange(anyLong(), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of());

        // When
        List<BaseCalendarEventResponse> responses = calendarEventService.getCalendarEventsByDateRange(memberId, startDate, endDate);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertInstanceOf(PersonalEventResponse.class, responses.get(0));
        assertEquals("단일 이벤트", responses.get(0).getTitle());
    }
}
