package com.memory.controller.diary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.controller.BaseIntegrationTest;
import com.memory.domain.diary.Diary;
import com.memory.domain.diary.repository.DiaryRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.diary.DiaryRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

class DiaryControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DiaryRepository diaryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member testMember;
    private Diary testDiary;
    private String validToken;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        String encodedPassword = passwordEncoder.encode("password123");
        testMember = new Member("Test User", "testuser", uniqueEmail, encodedPassword, MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
        
        testDiary = Diary.create(
            "Test Diary",
            "Test content",
            LocalDate.now(),
            "Happy",
            "Sunny",
            testMember
        );
        testDiary = diaryRepository.save(testDiary);
    }

    @Test
    @DisplayName("다이어리 생성 통합 테스트 - 성공")
    void createDiaryIntegrationSuccess() throws Exception {
        // Given
        DiaryRequest.Create request = new DiaryRequest.Create(
            "New Diary",
            "New content",
            LocalDate.now().plusDays(1),
            "Excited",
            "Cloudy"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/diaries")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value("New Diary"))
                .andExpect(jsonPath("$.data.content").value("New content"))
                .andExpect(jsonPath("$.data.mood").value("Excited"))
                .andExpect(jsonPath("$.data.weather").value("Cloudy"));
    }

    @Test
    @DisplayName("다이어리 생성 실패 - 인증 토큰 없음")
    void createDiaryFailNoAuth() throws Exception {
        // Given
        DiaryRequest.Create request = new DiaryRequest.Create(
            "New Diary", "New content", LocalDate.now(), "Happy", "Sunny"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/diaries")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("다이어리 생성 실패 - 잘못된 요청 데이터")
    void createDiaryFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/diaries")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("다이어리 수정 통합 테스트")
    void updateDiaryIntegrationTest() throws Exception {
        // Given
        DiaryRequest.Update request = new DiaryRequest.Update(
            "Updated Diary",
            "Updated content",
            LocalDate.now().plusDays(2),
            "Calm",
            "Rainy"
        );

        // When & Then
        mockMvc.perform(put("/api/v1/diaries/{diaryId}", testDiary.getId())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value("Updated Diary"))
                .andExpect(jsonPath("$.data.content").value("Updated content"))
                .andExpect(jsonPath("$.data.mood").value("Calm"))
                .andExpect(jsonPath("$.data.weather").value("Rainy"));
    }

    @Test
    @DisplayName("다이어리 삭제 통합 테스트")
    void deleteDiaryIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/diaries/{diaryId}", testDiary.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("기간별 다이어리 조회 통합 테스트")
    void getDiariesByDateRangeIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/diaries/date-range")
                        .header("Authorization", validToken)
                        .param("startDate", "2025-08-01")
                        .param("endDate", "2025-12-31"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("다이어리 수정 실패 - 다른 사용자의 다이어리")
    void updateDiaryFailNotOwner() throws Exception {
        // Given - 다른 사용자 생성
        String anotherEmail = "another" + System.currentTimeMillis() + "@example.com";
        Member anotherMember = new Member("Another User", "another", anotherEmail, "encodedPassword", MemberType.MEMBER);
        anotherMember = memberRepository.save(anotherMember);
        String anotherToken = "Bearer " + jwtTokenProvider.createAccessToken(anotherMember.getEmail());
        
        DiaryRequest.Update request = new DiaryRequest.Update(
            "Hacked Diary", "Hacked content", LocalDate.now(), "Evil", "Storm"
        );

        // When & Then
        mockMvc.perform(put("/api/v1/diaries/{diaryId}", testDiary.getId())
                        .header("Authorization", anotherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("다이어리 삭제 실패 - 다른 사용자의 다이어리")
    void deleteDiaryFailNotOwner() throws Exception {
        // Given - 다른 사용자 생성
        String anotherEmail = "another" + System.currentTimeMillis() + "@example.com";
        Member anotherMember = new Member("Another User", "another", anotherEmail, "encodedPassword", MemberType.MEMBER);
        anotherMember = memberRepository.save(anotherMember);
        String anotherToken = "Bearer " + jwtTokenProvider.createAccessToken(anotherMember.getEmail());

        // When & Then
        mockMvc.perform(delete("/api/v1/diaries/{diaryId}", testDiary.getId())
                        .header("Authorization", anotherToken))
                .andExpect(status().isBadRequest());
    }
}