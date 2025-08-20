package com.memory.controller.memory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.controller.BaseIntegrationTest;
import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;
import com.memory.domain.map.repository.MapRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.memory.Memory;
import com.memory.domain.memory.MemoryType;
import com.memory.domain.memory.repository.MemoryRepository;
import com.memory.dto.memory.MemoryRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

class MemoryControllerIntegrationTest extends BaseIntegrationTest {

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

    private Member testMember;
    private Map testMap;
    private Memory testMemory;
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
    }

    @Test
    @DisplayName("메모리 생성 통합 테스트 - 성공")
    void createMemoryIntegrationSuccess() throws Exception {
        // Given
        MemoryRequest.Create request = new MemoryRequest.Create(
            "New Memory",
            "New Content", 
            "New Location",
            LocalDate.now(),
            testMap.getId(),
            MemoryType.PUBLIC,
            Arrays.asList(1L, 2L),
            Arrays.asList("태그1", "태그2")
        );

        // When & Then
        mockMvc.perform(post("/api/v1/memories")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.title").value("New Memory"))
                .andExpect(jsonPath("$.data.content").value("New Content"));
    }

    @Test
    @DisplayName("메모리 생성 실패 - 인증 토큰 없음")
    void createMemoryFailNoAuth() throws Exception {
        // Given
        MemoryRequest.Create request = new MemoryRequest.Create(
            "New Memory", "New Content", "New Location", LocalDate.now(),
            testMap.getId(), MemoryType.PUBLIC, null, null
        );

        // When & Then
        mockMvc.perform(post("/api/v1/memories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("메모리 생성 실패 - 잘못된 요청 데이터")
    void createMemoryFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/memories")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("메모리 조회 통합 테스트")
    void findMemoryByIdIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/memories/{memoryId}", testMemory.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value(testMemory.getTitle()));
    }

    @Test
    @DisplayName("퍼블릭 메모리 조회 통합 테스트")
    void findPublicMemoryByIdIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/memories/public/{memoryId}", testMemory.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value(testMemory.getTitle()));
    }

    @Test
    @DisplayName("회원의 메모리 목록 조회 통합 테스트")
    void findMemoriesByMemberIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/memories/member")
                        .header("Authorization", validToken)
                        .param("size", "10")
                        .param("memoryType", "PUBLIC"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("메모리 수정 통합 테스트")
    void updateMemoryIntegrationTest() throws Exception {
        // Given
        MemoryRequest.Update request = new MemoryRequest.Update(
            "Updated Title",
            "Updated Content",
            "Updated Location", 
            LocalDate.now(),
            MemoryType.PRIVATE,
            null,
            null
        );

        // When & Then
        mockMvc.perform(put("/api/v1/memories/{memoryId}", testMemory.getId())
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.title").value("Updated Title"));
    }

    @Test
    @DisplayName("메모리 삭제 통합 테스트")
    void deleteMemoryIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/memories/{memoryId}", testMemory.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200));
    }

    @Test
    @DisplayName("공개 메모리 목록 조회 통합 테스트")
    void findPublicMemoriesIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/memories/public")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
}