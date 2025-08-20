package com.memory.controller.hashtag;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.controller.BaseIntegrationTest;
import com.memory.domain.hashtag.HashTag;
import com.memory.domain.hashtag.repository.HashTagRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

class HashTagControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private HashTagRepository hashTagRepository;

    private HashTag testHashTag;

    @BeforeEach
    void setUp() {
        testHashTag = HashTag.create("testhashtag");
        testHashTag.incrementUseCount(); // 사용 횟수 증가
        testHashTag = hashTagRepository.save(testHashTag);
    }

    @Test
    @DisplayName("해시태그 검색 통합 테스트 - 성공")
    void searchHashTagsIntegrationSuccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/hashtag/search")
                        .param("keyword", "test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("해시태그 검색 통합 테스트 - 제한 개수 지정")
    void searchHashTagsWithLimitIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/hashtag/search")
                        .param("keyword", "test")
                        .param("limit", "5"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("해시태그 검색 실패 - 키워드 누락")
    void searchHashTagsFailNoKeyword() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/hashtag/search"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("인기 해시태그 조회 통합 테스트 - 성공")
    void getPopularHashTagsIntegrationSuccess() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/hashtag/popular"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("인기 해시태그 조회 통합 테스트 - 제한 개수 지정")
    void getPopularHashTagsWithLimitIntegrationTest() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/hashtag/popular")
                        .param("limit", "3"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @DisplayName("존재하지 않는 키워드로 해시태그 검색")
    void searchHashTagsNotFound() throws Exception {
        // When & Then
        mockMvc.perform(get("/api/hashtag/search")
                        .param("keyword", "nonexistenthashtag"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data").isEmpty());
    }
}