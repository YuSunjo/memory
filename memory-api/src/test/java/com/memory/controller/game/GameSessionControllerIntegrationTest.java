package com.memory.controller.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.domain.game.GameMode;
import com.memory.domain.game.GameSession;
import com.memory.domain.game.GameSetting;
import com.memory.domain.game.repository.GameSessionRepository;
import com.memory.domain.game.repository.GameSettingRepository;
import com.memory.dto.game.GameSessionRequest;
import com.memory.config.jwt.JwtTokenProvider;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.member.MemberType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class GameSessionControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private GameSettingRepository gameSettingRepository;

    @Autowired
    private GameSessionRepository gameSessionRepository;

    private Member testMember;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Member 생성 및 저장 (고유한 이메일 생성)
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        testMember = new Member("Test User", "testuser", uniqueEmail, "encodedPassword", MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        // JWT 토큰 생성 (email로 - MemberIdResolver에서 email을 사용함)
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());

        // 게임 설정 초기화
        if (gameSettingRepository.findByGameModeAndIsActiveTrue(GameMode.RANDOM).isEmpty()) {
            GameSetting randomGameSetting = new GameSetting(GameMode.RANDOM, 10, 60, 1, "MAX(0, 1000 - (distance_km * 100))", true);
            gameSettingRepository.save(randomGameSetting);
        }
        
        if (gameSettingRepository.findByGameModeAndIsActiveTrue(GameMode.MY_MEMORIES).isEmpty()) {
            GameSetting myMemoriesGameSetting = new GameSetting(GameMode.MY_MEMORIES, 10, 60, 1, "MAX(0, 1000 - (distance_km * 100))", true);
            gameSettingRepository.save(myMemoriesGameSetting);
        }
    }


    @Test
    @DisplayName("게임 세션 생성 통합 테스트 - 성공")
    void createGameSessionIntegrationSuccess() throws Exception {
        // Given
        GameSessionRequest.Create request = new GameSessionRequest.Create(GameMode.RANDOM);

        // When & Then
        mockMvc.perform(post("/api/v1/game/sessions")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists());

    }

    @Test
    @DisplayName("게임 세션 생성 실패 - 인증 토큰 없음")
    void createGameSessionFailNoAuth() throws Exception {
        // Given
        GameSessionRequest.Create request = new GameSessionRequest.Create(GameMode.RANDOM);

        // When & Then
        mockMvc.perform(post("/api/v1/game/sessions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("게임 세션 생성 실패 - 잘못된 요청 데이터")
    void createGameSessionFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/game/sessions")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("게임 세션 조회 통합 테스트")
    void findGameSessionByIdIntegrationTest() throws Exception {
        // Given - 먼저 게임 세션 생성
        GameSession gameSession = new GameSession(testMember, GameMode.RANDOM);
        GameSession session = gameSessionRepository.save(gameSession);

        // sessionId 추출
        Long sessionId = session.getId();

        // When & Then
        mockMvc.perform(get("/api/v1/game/sessions/{sessionId}", sessionId)
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("현재 진행중인 게임 세션 조회 통합 테스트")
    void findProgressGameSessionIntegrationTest() throws Exception {
        // Given - 먼저 게임 세션 생성
        GameSession gameSession = new GameSession(testMember, GameMode.RANDOM);
        gameSessionRepository.save(gameSession);

        // When & Then
        mockMvc.perform(get("/api/v1/game/sessions/current")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("게임 세션 목록 조회 통합 테스트")
    void findGameSessionsByMemberIntegrationTest() throws Exception {
        // Given - 먼저 게임 세션 생성
        GameSession gameSession = new GameSession(testMember, GameMode.RANDOM);
        gameSessionRepository.save(gameSession);
        
        // When & Then
        mockMvc.perform(get("/api/v1/game/sessions")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("게임 세션 포기 통합 테스트")
    void giveUpGameSessionIntegrationTest() throws Exception {
        // Given - 먼저 게임 세션 생성
        GameSession gameSession = new GameSession(testMember, GameMode.RANDOM);
        GameSession session = gameSessionRepository.save(gameSession);

        Long sessionId = session.getId();

        // When & Then
        mockMvc.perform(patch("/api/v1/game/sessions/{sessionId}/give-up", sessionId)
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }
}