package com.memory.controller.comment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.comment.Comment;
import com.memory.domain.comment.repository.CommentRepository;
import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;
import com.memory.domain.map.repository.MapRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.memory.Memory;
import com.memory.domain.memory.MemoryType;
import com.memory.domain.memory.repository.MemoryRepository;
import com.memory.dto.comment.CommentCreateRequest;
import com.memory.dto.comment.CommentUpdateRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class CommentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;
    
    @Autowired
    private MapRepository mapRepository;
    
    @Autowired
    private MemoryRepository memoryRepository;
    
    @Autowired
    private CommentRepository commentRepository;

    private Member testMember;
    private Map testMap;
    private Memory testMemory;
    private Comment testComment;
    private String validToken;

    @BeforeEach
    void setUp() {
        // Member 생성 및 저장
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        testMember = new Member("Test User", "testuser", uniqueEmail, "encodedPassword", MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        // JWT 토큰 생성
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
        
        // Map 생성 및 저장
        testMap = Map.builder()
                .name("Test Map")
                .description("Test Description")
                .address("Test Address")
                .latitude("37.5665")
                .longitude("126.9780")
                .mapType(MapType.USER_PLACE)
                .member(testMember)
                .build();
        testMap = mapRepository.save(testMap);
        
        // Memory 생성 및 저장
        testMemory = new Memory("Test Memory", "Test Content", "Test Location", LocalDate.now(), MemoryType.PUBLIC, testMember, testMap);
        testMemory = memoryRepository.save(testMemory);
        
        // Comment 생성 및 저장
        testComment = Comment.create("Test Comment", testMemory, testMember, null);
        testComment = commentRepository.save(testComment);
    }

    @Test
    @DisplayName("댓글 생성 통합 테스트 - 성공")
    void createCommentIntegrationSuccess() throws Exception {
        // Given
        CommentCreateRequest request = new CommentCreateRequest(testMemory.getId(), "New Comment Content", null);

        // When & Then
        mockMvc.perform(post("/api/v1/comments")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.content").value("New Comment Content"));
    }

    @Test
    @DisplayName("대댓글 생성 통합 테스트 - 성공")
    void createReplyCommentIntegrationSuccess() throws Exception {
        // Given
        CommentCreateRequest request = new CommentCreateRequest(testMemory.getId(), "Reply Comment", testComment.getId());

        // When & Then
        mockMvc.perform(post("/api/v1/comments")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.content").value("Reply Comment"));
    }

    @Test
    @DisplayName("댓글 생성 실패 - 인증 토큰 없음")
    void createCommentFailNoAuth() throws Exception {
        // Given
        CommentCreateRequest request = new CommentCreateRequest(testMemory.getId(), "New Comment", null);

        // When & Then
        mockMvc.perform(post("/api/v1/comments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("댓글 생성 실패 - 잘못된 요청 데이터")
    void createCommentFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/comments")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메모리의 최상위 댓글 목록 조회 통합 테스트")
    void getTopLevelCommentsByMemoryIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/comments/memory/{memoryId}/top-level", testMemory.getId())
                        .header("Authorization", validToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.comments").isArray());
    }

    @Test
    @DisplayName("퍼블릭 메모리의 최상위 댓글 목록 조회 통합 테스트")
    void getTopLevelCommentsByPublicMemoryIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/comments/memory/public/{memoryId}/top-level", testMemory.getId())
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("특정 댓글의 대댓글 목록 조회 통합 테스트")
    void getRepliesByCommentIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/comments/{commentId}/replies", testComment.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("댓글 상세 조회 통합 테스트")
    void getCommentIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/comments/{commentId}", testComment.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.content").value(testComment.getContent()));
    }

    @Test
    @DisplayName("댓글 수정 통합 테스트")
    void updateCommentIntegrationTest() throws Exception {
        // Given
        CommentUpdateRequest request = new CommentUpdateRequest("Updated Comment Content");

        // When & Then
        mockMvc.perform(put("/api/v1/comments/{commentId}", testComment.getId())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.content").value("Updated Comment Content"));
    }

    @Test
    @DisplayName("댓글 삭제 통합 테스트")
    void deleteCommentIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/comments/{commentId}", testComment.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("멤버의 댓글 목록 조회 통합 테스트")
    void getCommentsByMemberIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/comments/member/{memberId}", testMember.getId())
                        .header("Authorization", validToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("최근 댓글 조회 통합 테스트")
    void getRecentCommentsIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/comments/recent")
                        .header("Authorization", validToken)
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }
}