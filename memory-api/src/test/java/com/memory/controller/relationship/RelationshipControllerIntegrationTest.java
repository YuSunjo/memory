package com.memory.controller.relationship;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.controller.BaseIntegrationTest;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.relationship.Relationship;
import com.memory.domain.relationship.RelationshipStatus;
import com.memory.domain.relationship.repository.RelationshipRepository;
import com.memory.dto.relationship.RelationshipRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

class RelationshipControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RelationshipRepository relationshipRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member testMember;
    private Member targetMember;
    private Relationship testRelationship;
    private String validToken;
    private String targetToken;

    @BeforeEach
    void setUp() {
        String uniqueEmail1 = "test1" + System.currentTimeMillis() + "@example.com";
        String uniqueEmail2 = "test2" + System.currentTimeMillis() + "@example.com";
        String encodedPassword = passwordEncoder.encode("password123");
        
        testMember = new Member("Test User", "testuser", uniqueEmail1, encodedPassword, MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        targetMember = new Member("Target User", "targetuser", uniqueEmail2, encodedPassword, MemberType.MEMBER);
        targetMember = memberRepository.save(targetMember);
        
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
        targetToken = "Bearer " + jwtTokenProvider.createAccessToken(targetMember.getEmail());
    }

    @Test
    @DisplayName("관계 요청 생성 통합 테스트 - 성공")
    void createRelationshipRequestIntegrationSuccess() throws Exception {
        // Given
        RelationshipRequest.Create request = new RelationshipRequest.Create(targetMember.getId());

        // When & Then
        mockMvc.perform(post("/api/v1/relationship/request")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.relationshipStatus").value("PENDING"));
    }

    @Test
    @DisplayName("관계 요청 생성 실패 - 인증 토큰 없음")
    void createRelationshipRequestFailNoAuth() throws Exception {
        // Given
        RelationshipRequest.Create request = new RelationshipRequest.Create(targetMember.getId());

        // When & Then
        mockMvc.perform(post("/api/v1/relationship/request")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("관계 요청 수락 통합 테스트")
    void acceptRelationshipRequestIntegrationTest() throws Exception {
        // Given - 관계 요청 생성
        testRelationship = Relationship.createRelationship(testMember, targetMember, RelationshipStatus.PENDING);
        testRelationship = relationshipRepository.save(testRelationship);

        // When & Then
        mockMvc.perform(post("/api/v1/relationship/accept/{relationshipId}", testRelationship.getId())
                        .header("Authorization", targetToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.relationshipStatus").value("ACCEPTED"));
    }

    @Test
    @DisplayName("관계 목록 조회 통합 테스트")
    void getRelationshipsIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/relationship")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("관계 상태별 조회 통합 테스트")
    void getRelationshipsByStatusIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/relationship/status")
                        .header("Authorization", validToken)
                        .param("status", "PENDING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("받은 관계 목록 조회 통합 테스트")
    void getReceivedRelationshipsIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/relationship/received")
                        .header("Authorization", targetToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("받은 관계 상태별 조회 통합 테스트")
    void getReceivedRelationshipsByStatusIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/relationship/received/status")
                        .header("Authorization", targetToken)
                        .param("status", "PENDING"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("관계 종료 통합 테스트")
    void endRelationshipIntegrationTest() throws Exception {
        // Given - 관계 생성 후 수락 상태로 변경
        testRelationship = Relationship.createRelationship(testMember, targetMember, RelationshipStatus.PENDING);
        testRelationship.accept();
        testRelationship = relationshipRepository.save(testRelationship);

        // When & Then
        mockMvc.perform(post("/api/v1/relationship/end/{relationshipId}", testRelationship.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.relationshipStatus").value("ENDED"));
    }

    @Test
    @DisplayName("관계 요청 생성 실패 - 잘못된 요청 데이터")
    void createRelationshipRequestFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/relationship/request")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("존재하지 않는 관계 수락 시도")
    void acceptNonExistentRelationship() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/relationship/accept/{relationshipId}", 99999L)
                        .header("Authorization", validToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("잘못된 관계 상태로 조회")
    void getRelationshipsByInvalidStatus() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/relationship/status")
                        .header("Authorization", validToken)
                        .param("status", "INVALID_STATUS"))
                .andExpect(status().isInternalServerError());
    }
}