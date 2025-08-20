package com.memory.controller.memberlink;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.memberlink.MemberLink;
import com.memory.domain.memberlink.repository.MemberLinkRepository;
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
class MemberLinkControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemberLinkRepository memberLinkRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member testMember;
    private MemberLink testMemberLink;
    private String validToken;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        String encodedPassword = passwordEncoder.encode("password123");
        testMember = new Member("Test User", "testuser", uniqueEmail, encodedPassword, MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
        
        testMemberLink = MemberLink.builder()
            .member(testMember)
            .title("Test Link")
            .url("https://example.com")
            .description("Test description")
            .displayOrder(1)
            .isActive(true)
            .isVisible(true)
            .iconUrl("https://example.com/icon.png")
            .build();
        testMemberLink = memberLinkRepository.save(testMemberLink);
    }

    @Test
    @DisplayName("링크 생성 통합 테스트 - 성공")
    void createMemberLinkIntegrationSuccess() throws Exception {
        // Given
        String requestJson = """
            {
                "title": "New Link",
                "url": "https://newexample.com",
                "description": "New description",
                "isActive": true,
                "isVisible": true,
                "iconUrl": "https://newexample.com/icon.png"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/v1/member-links")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value("New Link"))
                .andExpect(jsonPath("$.data.url").value("https://newexample.com"));
    }

    @Test
    @DisplayName("링크 생성 실패 - 인증 토큰 없음")
    void createMemberLinkFailNoAuth() throws Exception {
        // Given
        String requestJson = """
            {
                "title": "New Link",
                "url": "https://newexample.com"
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/v1/member-links")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("링크 수정 통합 테스트")
    void updateMemberLinkIntegrationTest() throws Exception {
        // Given
        String requestJson = """
            {
                "title": "Updated Link",
                "url": "https://updated.com",
                "description": "Updated description",
                "displayOrder": 1,
                "isActive": true,
                "isVisible": false,
                "iconUrl": "https://updated.com/icon.png"
            }
            """;

        // When & Then
        mockMvc.perform(put("/api/v1/member-links/{linkId}", testMemberLink.getId())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value("Updated Link"))
                .andExpect(jsonPath("$.data.url").value("https://updated.com"));
    }

    @Test
    @DisplayName("링크 순서 변경 통합 테스트")
    void updateMemberLinkOrderIntegrationTest() throws Exception {
        // Given - 두 번째 링크 생성
        MemberLink secondLink = MemberLink.builder()
            .member(testMember)
            .title("Second Link")
            .url("https://second.com")
            .description("Second description")
            .displayOrder(2)
            .isActive(true)
            .isVisible(true)
            .build();
        memberLinkRepository.save(secondLink);
        
        String requestJson = """
            {
                "displayOrder": 2
            }
            """;

        // When & Then
        mockMvc.perform(patch("/api/v1/member-links/{linkId}/order", testMemberLink.getId())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.displayOrder").value(2));
    }

    @Test
    @DisplayName("내 링크 목록 조회 통합 테스트")
    void getMemberLinksIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/member-links")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("공개 링크 목록 조회 통합 테스트")
    void getPublicMemberLinksIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/members/{memberId}/links", testMember.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("링크 삭제 통합 테스트")
    void deleteMemberLinkIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/member-links/{linkId}", testMemberLink.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("링크 클릭 카운트 증가 통합 테스트")
    void incrementClickCountIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/member-links/{linkId}/click", testMemberLink.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("링크 생성 실패 - 잘못된 요청 데이터")
    void createMemberLinkFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/member-links")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 링크 수정 시도")
    void updateNonExistentMemberLink() throws Exception {
        // Given
        String requestJson = """
            {
                "title": "Updated",
                "url": "https://updated.com",
                "displayOrder": 1,
                "isActive": true,
                "isVisible": true
            }
            """;

        // When & Then
        mockMvc.perform(put("/api/v1/member-links/{linkId}", 99999L)
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }
}