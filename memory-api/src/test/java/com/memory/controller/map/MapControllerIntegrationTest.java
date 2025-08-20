package com.memory.controller.map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.controller.BaseIntegrationTest;
import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;
import com.memory.domain.map.repository.MapRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.map.MapRequest;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

class MapControllerIntegrationTest extends BaseIntegrationTest {

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
    private PasswordEncoder passwordEncoder;

    private Member testMember;
    private Map testMap;
    private String validToken;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        String encodedPassword = passwordEncoder.encode("password123");
        testMember = new Member("Test User", "testuser", uniqueEmail, encodedPassword, MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
        
        testMap = Map.builder()
                .name("Test Map")
                .description("Test Description")
                .address("Test Address")
                .latitude("37.5665")
                .longitude("126.9780")
                .mapType(MapType.FESTIVAL)
                .member(testMember)
                .build();
        testMap = mapRepository.save(testMap);
    }

    @Test
    @DisplayName("지도 생성 통합 테스트 - 성공")
    void createMapIntegrationSuccess() throws Exception {
        // Given
        MapRequest.Create request = new MapRequest.Create(
            "New Map",
            "New Description",
            "Seoul, Korea",
            "37.5665",
            "126.9780",
            MapType.USER_PLACE
        );

        // When & Then
        mockMvc.perform(post("/api/v1/maps")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name").value("New Map"))
                .andExpect(jsonPath("$.data.description").value("New Description"))
                .andExpect(jsonPath("$.data.address").value("Seoul, Korea"));
    }

    @Test
    @DisplayName("지도 생성 실패 - 인증 토큰 없음")
    void createMapFailNoAuth() throws Exception {
        // Given
        MapRequest.Create request = new MapRequest.Create(
            "New Map", "Description", "Address", "37.5665", "126.9780", MapType.FESTIVAL
        );

        // When & Then
        mockMvc.perform(post("/api/v1/maps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("지도 생성 실패 - 잘못된 요청 데이터")
    void createMapFailInvalidRequest() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/v1/maps")
                        .header("Authorization", validToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("지도 조회 통합 테스트 - ID로 조회")
    void findMapByIdIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/maps/{mapId}", testMap.getId()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name").value("Test Map"))
                .andExpect(jsonPath("$.data.description").value("Test Description"));
    }

    @Test
    @DisplayName("지도 목록 조회 통합 테스트 - FESTIVAL 타입")
    void findMapsByTypeIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/maps")
                        .param("mapType", "FESTIVAL"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("지도 목록 조회 실패 - USER_PLACE 타입 조회")
    void findMapsByTypeFailUserPlace() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/maps")
                        .param("mapType", "USER_PLACE"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("회원의 지도 목록 조회 통합 테스트")
    void findMapsByMemberIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/maps/member")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("회원의 지도 목록 조회 실패 - 인증 토큰 없음")
    void findMapsByMemberFailNoAuth() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/maps/member"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("존재하지 않는 지도 조회")
    void findMapByIdNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/v1/maps/{mapId}", 99999L))
                .andExpect(status().isNotFound());
    }
}