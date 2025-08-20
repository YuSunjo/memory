package com.memory.controller.calendar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.calendar.CalendarEventType;
import com.memory.domain.calendar.PersonalEvent;
import com.memory.domain.calendar.repository.PersonalEventRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.calendar.CalendarEventRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CalendarEventControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private PersonalEventRepository personalEventRepository;

    private Member testMember;
    private PersonalEvent testPersonalEvent;
    private String validToken;

    @BeforeEach
    void setUp() {
        // ObjectMapper JavaTimeModule 등록
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        
        // Member 생성 및 저장
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        testMember = new Member("Test User", "testuser", uniqueEmail, "encodedPassword", MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        // JWT 토큰 생성
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
        
        // PersonalEvent 생성 및 저장
        testPersonalEvent = PersonalEvent.create(
            "Test Personal Event",
            "Test Description", 
            LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusHours(2),
            "Test Location",
            testMember
        );
        testPersonalEvent = personalEventRepository.save(testPersonalEvent);
    }

    @Test
    @DisplayName("개인 일정 생성 통합 테스트 - 성공")
    void createPersonalEventIntegrationSuccess() throws Exception {
        // Given
        CalendarEventRequest.Create request = new CalendarEventRequest.Create(
            "New Personal Event",
            "New Description",
            LocalDateTime.now().plusDays(2),
            LocalDateTime.now().plusDays(2).plusHours(3),
            "New Location",
            CalendarEventType.PERSONAL,
            false
        );

        // When & Then
        mockMvc.perform(post("/api/v1/calendar/events")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value("New Personal Event"))
                .andExpect(jsonPath("$.data.description").value("New Description"));
    }

    @Test
    @DisplayName("일정 생성 실패 - 인증 토큰 없음")
    void createCalendarEventFailNoAuth() throws Exception {
        // Given
        CalendarEventRequest.Create request = new CalendarEventRequest.Create(
            "New Event", "Description", LocalDateTime.now().plusDays(1),
            LocalDateTime.now().plusDays(1).plusHours(2), "Location",
            CalendarEventType.PERSONAL, false
        );

        // When & Then
        mockMvc.perform(post("/api/v1/calendar/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("일정 생성 실패 - 잘못된 요청 데이터")
    void createCalendarEventFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/calendar/events")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("일정 수정 통합 테스트")
    void updateCalendarEventIntegrationTest() throws Exception {
        // Given
        CalendarEventRequest.Update request = new CalendarEventRequest.Update(
            "Updated Personal Event",
            "Updated Description",
            LocalDateTime.now().plusDays(3),
            LocalDateTime.now().plusDays(3).plusHours(2),
            "Updated Location",
            CalendarEventType.PERSONAL,
            false
        );

        // When & Then
        mockMvc.perform(put("/api/v1/calendar/events/{eventId}", testPersonalEvent.getId())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value("Updated Personal Event"));
    }

    @Test
    @DisplayName("특정 기간 내 일정 조회 통합 테스트")
    void getCalendarEventsByDateRangeIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/calendar/events")
                        .header("Authorization", validToken)
                        .param("startDate", "2025-08-01")
                        .param("endDate", "2025-12-31"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("D-day 조회 통합 테스트")
    void getCalendarEventsWithDdayIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/calendar/events/dday")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
}