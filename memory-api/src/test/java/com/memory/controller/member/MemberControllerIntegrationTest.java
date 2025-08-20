package com.memory.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.member.MemberRequest;
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
class MemberControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member testMember;
    private String validToken;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        String encodedPassword = passwordEncoder.encode("password123");
        testMember = new Member("Test User", "testuser", uniqueEmail, encodedPassword, MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
    }

    @Test
    @DisplayName("회원가입 통합 테스트 - 성공")
    void signupIntegrationSuccess() throws Exception {
        // Given
        String uniqueEmail = "signup" + System.currentTimeMillis() + "@example.com";
        MemberRequest.Signup request = new MemberRequest.Signup(
            uniqueEmail, "password123", "New User", "newuser"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.email").value(uniqueEmail))
                .andExpect(jsonPath("$.data.name").value("New User"))
                .andExpect(jsonPath("$.data.nickname").value("newuser"));
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 요청 데이터")
    void signupFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/member/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("로그인 통합 테스트 - 성공")  
    void loginIntegrationSuccess() throws Exception {
        // Given
        MemberRequest.Login request = new MemberRequest.Login(
            testMember.getEmail(), "password123"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void loginFailWrongPassword() throws Exception {
        // Given
        MemberRequest.Login request = new MemberRequest.Login(
            testMember.getEmail(), "wrongpassword"
        );

        // When & Then
        mockMvc.perform(post("/api/v1/member/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("내 정보 조회 통합 테스트")
    void findMemberIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/member/me")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.email").value(testMember.getEmail()))
                .andExpect(jsonPath("$.data.name").value(testMember.getName()));
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 인증 토큰 없음")
    void findMemberFailNoAuth() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/member/me"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("회원 정보 수정 통합 테스트")
    void updateMemberIntegrationTest() throws Exception {
        // Given
        MemberRequest.Update request = new MemberRequest.Update("updatedNickname", 1L);

        // When & Then
        mockMvc.perform(put("/api/v1/member/me")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.nickname").value("updatedNickname"));
    }

    @Test
    @DisplayName("비밀번호 변경 통합 테스트")
    void updatePasswordIntegrationTest() throws Exception {
        // Given
        MemberRequest.PasswordUpdate request = new MemberRequest.PasswordUpdate("newPassword123");

        // When & Then
        mockMvc.perform(put("/api/v1/member/me/password")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("이메일로 회원 조회 통합 테스트")
    void findMemberByEmailIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/member/email")
                        .param("email", testMember.getEmail()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.email").value(testMember.getEmail()));
    }
}