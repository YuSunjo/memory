package com.memory.controller.search;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.controller.BaseIntegrationTest;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.memory.Memory;
import com.memory.domain.memory.MemoryType;
import com.memory.domain.memory.repository.MemoryRepository;
import com.memory.dto.search.MemorySearchRequest;
import com.memory.dto.search.SearchType;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

class MemorySearchControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private MemoryRepository memoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member testMember;
    private Memory testMemory;
    private String validToken;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        String encodedPassword = passwordEncoder.encode("password123");
        testMember = new Member("Test User", "testuser", uniqueEmail, encodedPassword, MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
        
        testMemory = Memory.builder()
            .title("Test Memory")
            .content("This is a test memory content")
            .member(testMember)
            .memoryType(MemoryType.PUBLIC)
            .build();
        testMemory = memoryRepository.save(testMemory);
    }

    @Test
    @DisplayName("인증된 사용자 메모리 검색 통합 테스트 - 성공")
    void searchMyMemoriesIntegrationSuccess() throws Exception {
        // Given
        MemorySearchRequest request = MemorySearchRequest.builder()
            .type(SearchType.ALL)
            .query("test")
            .page(0)
            .size(20)
            .highlight(true)
            .build();

        // When & Then
        mockMvc.perform(post("/api/v1/memories/search")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.pageInfo.totalElements").exists())
                .andExpect(jsonPath("$.data.memories").isArray());
    }

    @Test
    @DisplayName("인증된 사용자 메모리 검색 실패 - 인증 토큰 없음")
    void searchMyMemoriesFailNoAuth() throws Exception {
        // Given
        MemorySearchRequest request = MemorySearchRequest.builder()
            .type(SearchType.ALL)
            .query("test")
            .build();

        // When & Then
        mockMvc.perform(post("/api/v1/memories/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("공개 메모리 검색 통합 테스트 - 성공")
    void searchPublicMemoriesIntegrationSuccess() throws Exception {
        // Given
        MemorySearchRequest request = MemorySearchRequest.builder()
            .type(SearchType.ALL)
            .query("test")
            .page(0)
            .size(20)
            .highlight(true)
            .build();

        // When & Then
        mockMvc.perform(post("/api/v1/memories/public/search")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.pageInfo.totalElements").exists())
                .andExpect(jsonPath("$.data.memories").isArray());
    }

    @Test
    @DisplayName("제목으로 메모리 검색 통합 테스트")
    void searchMemoriesByTitleIntegrationTest() throws Exception {
        // Given
        MemorySearchRequest request = MemorySearchRequest.builder()
            .type(SearchType.TITLE)
            .query("Test Memory")
            .page(0)
            .size(20)
            .highlight(true)
            .build();

        // When & Then
        mockMvc.perform(post("/api/v1/memories/search")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("날짜 범위로 메모리 검색 통합 테스트")
    void searchMemoriesByDateRangeIntegrationTest() throws Exception {
        // Given
        MemorySearchRequest request = MemorySearchRequest.builder()
            .type(SearchType.DATE)
            .fromDate(LocalDate.now().minusDays(1))
            .toDate(LocalDate.now().plusDays(1))
            .page(0)
            .size(20)
            .highlight(true)
            .build();

        // When & Then
        mockMvc.perform(post("/api/v1/memories/search")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("인증된 사용자 자동완성 통합 테스트")
    void getAuthenticatedAutocompleteIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/memories/autocomplete")
                        .header("Authorization", validToken)
                        .param("query", "test")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("공개 메모리 자동완성 통합 테스트")
    void getPublicAutocompleteIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/memories/public/autocomplete")
                        .param("query", "test")
                        .param("limit", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @DisplayName("자동완성 실패 - 인증 토큰 없음")
    void getAutocompleteFailNoAuth() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/memories/autocomplete")
                        .param("query", "test")
                        .param("limit", "10"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("메모리 검색 실패 - 잘못된 요청 데이터")
    void searchMemoriesFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/memories/search")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("해시태그로 메모리 검색 통합 테스트")
    void searchMemoriesByHashtagsIntegrationTest() throws Exception {
        // Given
        String requestJson = """
            {
                "type": "HASHTAGS",
                "hashtags": ["test", "memory"],
                "page": 0,
                "size": 20,
                "highlight": true
            }
            """;

        // When & Then
        mockMvc.perform(post("/api/v1/memories/search")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists());
    }
}