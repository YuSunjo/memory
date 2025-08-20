package com.memory.controller.routine;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.routine.Routine;
import com.memory.domain.routine.repository.RoutineRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class RoutineControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member testMember;
    private Routine testRoutine;
    private String validToken;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        String encodedPassword = passwordEncoder.encode("password123");
        testMember = new Member("Test User", "testuser", uniqueEmail, encodedPassword, MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
        
        testRoutine = Routine.create(
            "Test Routine",
            "Test routine content",
            testMember,
            null
        );
        testRoutine = routineRepository.save(testRoutine);
    }

    @Test
    @DisplayName("루틴 생성 통합 테스트 - 성공")
    void createRoutineIntegrationSuccess() throws Exception {
        // Given
        String requestJson = """
            {
                "title": "New Routine",
                "content": "New routine content",
                "repeatType": "DAILY",
                "interval": 1,
                "startDate": "2025-08-20",
                "endDate": "2025-12-31"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/v1/routine")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value("New Routine"))
                .andExpect(jsonPath("$.data.content").value("New routine content"));
    }

    @Test
    @DisplayName("루틴 생성 실패 - 인증 토큰 없음")
    void createRoutineFailNoAuth() throws Exception {
        // Given
        String requestJson = """
            {
                "title": "New Routine",
                "content": "New routine content",
                "repeatType": "DAILY",
                "interval": 1
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/v1/routine")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("루틴 수정 통합 테스트")
    void updateRoutineIntegrationTest() throws Exception {
        // Given
        String requestJson = """
            {
                "title": "Updated Routine",
                "content": "Updated routine content",
                "repeatType": "WEEKLY",
                "interval": 2,
                "startDate": "2025-08-20",
                "endDate": "2025-12-31"
            }
            """;

        // When & Then
        mockMvc.perform(put("/api/v1/routine/{routineId}", testRoutine.getId())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value("Updated Routine"))
                .andExpect(jsonPath("$.data.content").value("Updated routine content"));
    }

    @Test
    @DisplayName("루틴 삭제 통합 테스트")
    void deleteRoutineIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/routine/{routineId}", testRoutine.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("루틴 활성화/비활성화 통합 테스트")
    void toggleRoutineActiveIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(patch("/api/v1/routine/{routineId}/toggle", testRoutine.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("루틴 목록 조회 통합 테스트")
    void getRoutinesIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/routine")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("루틴 생성 실패 - 잘못된 요청 데이터")
    void createRoutineFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/routine")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("존재하지 않는 루틴 수정 시도")
    void updateNonExistentRoutine() throws Exception {
        // Given
        String requestJson = """
            {
                "title": "Updated",
                "content": "Updated content",
                "repeatType": "DAILY",
                "interval": 1
            }
            """;

        // When & Then
        mockMvc.perform(put("/api/v1/routine/{routineId}", 99999L)
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("주간 반복 루틴 생성 테스트")
    void createWeeklyRoutineIntegrationTest() throws Exception {
        // Given
        String requestJson = """
            {
                "title": "Weekly Routine",
                "content": "Weekly routine content",
                "repeatType": "WEEKLY",
                "interval": 1,
                "startDate": "2025-08-20",
                "endDate": "2025-12-31"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/v1/routine")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value("Weekly Routine"));
    }

    @Test
    @DisplayName("반복 없는 루틴 생성 테스트")
    void createNoneRepeatRoutineIntegrationTest() throws Exception {
        // Given
        String requestJson = """
            {
                "title": "No Repeat Routine",
                "content": "No repeat routine content",
                "repeatType": "NONE",
                "interval": null,
                "startDate": "2025-08-20",
                "endDate": null
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/v1/routine")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value("No Repeat Routine"));
    }
}