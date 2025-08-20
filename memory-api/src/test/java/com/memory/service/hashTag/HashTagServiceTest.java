package com.memory.service.hashTag;

import com.memory.domain.hashtag.HashTag;
import com.memory.domain.hashtag.MemoryHashTag;
import com.memory.domain.hashtag.repository.HashTagRepository;
import com.memory.domain.hashtag.repository.MemoryHashTagRepository;
import com.memory.domain.memory.Memory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class HashTagServiceTest {

    @Mock
    private HashTagRepository hashTagRepository;

    @Mock
    private MemoryHashTagRepository memoryHashTagRepository;

    @InjectMocks
    private HashTagService hashTagService;

    private HashTag existingHashTag;
    private HashTag newHashTag;
    private Memory memory;
    private MemoryHashTag memoryHashTag;

    private final Long hashTagId = 1L;
    private final String existingHashTagName = "기존해시태그";
    private final String newHashTagName = "새해시태그";

    @BeforeEach
    void setUp() {
        existingHashTag = HashTag.create(existingHashTagName);
        setId(existingHashTag, hashTagId);

        newHashTag = HashTag.create(newHashTagName);
        setId(newHashTag, 2L);

        memory = mock(Memory.class);
        memoryHashTag = new MemoryHashTag(memory, existingHashTag);
        setId(memoryHashTag, 1L);
    }

    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @Test
    @DisplayName("해시태그 찾기 또는 생성 성공 테스트 - 기존 해시태그 찾기")
    void findOrCreateHashTagsSuccessExisting() {
        // Given
        List<String> hashTagNames = List.of(existingHashTagName);
        when(hashTagRepository.findByName(existingHashTagName)).thenReturn(Optional.of(existingHashTag));

        // When
        List<HashTag> result = hashTagService.findOrCreateHashTags(hashTagNames);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(existingHashTagName, result.get(0).getName());

        verify(hashTagRepository).findByName(existingHashTagName);
        verify(hashTagRepository, never()).save(any(HashTag.class));
    }

    @Test
    @DisplayName("해시태그 찾기 또는 생성 성공 테스트 - 새 해시태그 생성")
    void findOrCreateHashTagsSuccessCreateNew() {
        // Given
        List<String> hashTagNames = List.of(newHashTagName);
        when(hashTagRepository.findByName(newHashTagName)).thenReturn(Optional.empty());
        when(hashTagRepository.save(any(HashTag.class))).thenReturn(newHashTag);

        // When
        List<HashTag> result = hashTagService.findOrCreateHashTags(hashTagNames);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(newHashTagName, result.get(0).getName());

        verify(hashTagRepository).findByName(newHashTagName);
        verify(hashTagRepository).save(any(HashTag.class));
    }

    @Test
    @DisplayName("해시태그 찾기 또는 생성 성공 테스트 - 혼합 케이스")
    void findOrCreateHashTagsSuccessMixed() {
        // Given
        List<String> hashTagNames = Arrays.asList(existingHashTagName, newHashTagName);
        when(hashTagRepository.findByName(existingHashTagName)).thenReturn(Optional.of(existingHashTag));
        when(hashTagRepository.findByName(newHashTagName)).thenReturn(Optional.empty());
        when(hashTagRepository.save(any(HashTag.class))).thenReturn(newHashTag);

        // When
        List<HashTag> result = hashTagService.findOrCreateHashTags(hashTagNames);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(hashTagRepository).findByName(existingHashTagName);
        verify(hashTagRepository).findByName(newHashTagName);
        verify(hashTagRepository).save(any(HashTag.class));
    }

    @Test
    @DisplayName("해시태그 찾기 또는 생성 테스트 - null 입력")
    void findOrCreateHashTagsWithNullInput() {
        // When
        List<HashTag> result = hashTagService.findOrCreateHashTags(null);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hashTagRepository, never()).findByName(anyString());
        verify(hashTagRepository, never()).save(any(HashTag.class));
    }

    @Test
    @DisplayName("해시태그 찾기 또는 생성 테스트 - 빈 리스트 입력")
    void findOrCreateHashTagsWithEmptyList() {
        // When
        List<HashTag> result = hashTagService.findOrCreateHashTags(Collections.emptyList());

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hashTagRepository, never()).findByName(anyString());
        verify(hashTagRepository, never()).save(any(HashTag.class));
    }

    @Test
    @DisplayName("메모리 해시태그 사용 횟수 감소 성공 테스트")
    void decrementUseCountForMemoryHashTagsSuccess() {
        // Given
        List<MemoryHashTag> memoryHashTags = List.of(memoryHashTag);
        when(memoryHashTagRepository.findByMemory(memory)).thenReturn(memoryHashTags);

        // When
        hashTagService.decrementUseCountForMemoryHashTags(memory);

        // Then
        verify(memoryHashTagRepository).findByMemory(memory);
    }

    @Test
    @DisplayName("메모리 해시태그 사용 횟수 감소 테스트 - 빈 리스트")
    void decrementUseCountForMemoryHashTagsEmptyList() {
        // Given
        when(memoryHashTagRepository.findByMemory(memory)).thenReturn(Collections.emptyList());

        // When
        hashTagService.decrementUseCountForMemoryHashTags(memory);

        // Then
        verify(memoryHashTagRepository).findByMemory(memory);
    }

    @Test
    @DisplayName("해시태그 이름으로 검색 성공 테스트")
    void searchHashTagsByNameSuccess() {
        // Given
        String keyword = "테스트";
        int limit = 10;
        List<HashTag> expectedHashTags = Arrays.asList(existingHashTag, newHashTag);
        when(hashTagRepository.findHashTagsByNameContaining(keyword, limit)).thenReturn(expectedHashTags);

        // When
        List<HashTag> result = hashTagService.searchHashTagsByName(keyword, limit);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(expectedHashTags, result);

        verify(hashTagRepository).findHashTagsByNameContaining(keyword, limit);
    }

    @Test
    @DisplayName("해시태그 이름으로 검색 테스트 - 결과 없음")
    void searchHashTagsByNameNoResults() {
        // Given
        String keyword = "존재하지않는키워드";
        int limit = 10;
        when(hashTagRepository.findHashTagsByNameContaining(keyword, limit)).thenReturn(Collections.emptyList());

        // When
        List<HashTag> result = hashTagService.searchHashTagsByName(keyword, limit);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hashTagRepository).findHashTagsByNameContaining(keyword, limit);
    }

    @Test
    @DisplayName("인기 해시태그 조회 성공 테스트")
    void getPopularHashTagsSuccess() {
        // Given
        int limit = 5;
        List<HashTag> popularHashTags = Arrays.asList(existingHashTag, newHashTag);
        when(hashTagRepository.findPopularHashTags(limit)).thenReturn(popularHashTags);

        // When
        List<HashTag> result = hashTagService.getPopularHashTags(limit);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(popularHashTags, result);

        verify(hashTagRepository).findPopularHashTags(limit);
    }

    @Test
    @DisplayName("인기 해시태그 조회 테스트 - 결과 없음")
    void getPopularHashTagsNoResults() {
        // Given
        int limit = 5;
        when(hashTagRepository.findPopularHashTags(limit)).thenReturn(Collections.emptyList());

        // When
        List<HashTag> result = hashTagService.getPopularHashTags(limit);

        // Then
        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(hashTagRepository).findPopularHashTags(limit);
    }

    @Test
    @DisplayName("다양한 검색 제한 수 테스트")
    void searchWithDifferentLimits() {
        // Given
        String keyword = "테스트";
        List<HashTag> hashTags = List.of(existingHashTag);

        // When & Then - limit 1
        when(hashTagRepository.findHashTagsByNameContaining(keyword, 1)).thenReturn(hashTags);
        List<HashTag> result1 = hashTagService.searchHashTagsByName(keyword, 1);
        assertEquals(1, result1.size());

        // When & Then - limit 20
        when(hashTagRepository.findHashTagsByNameContaining(keyword, 20)).thenReturn(hashTags);
        List<HashTag> result20 = hashTagService.searchHashTagsByName(keyword, 20);
        assertEquals(1, result20.size());

        verify(hashTagRepository).findHashTagsByNameContaining(keyword, 1);
        verify(hashTagRepository).findHashTagsByNameContaining(keyword, 20);
    }

    @Test
    @DisplayName("동일한 해시태그 이름 중복 처리 테스트")
    void findOrCreateHashTagsWithDuplicateNames() {
        // Given
        List<String> hashTagNames = Arrays.asList(existingHashTagName, existingHashTagName);
        when(hashTagRepository.findByName(existingHashTagName)).thenReturn(Optional.of(existingHashTag));

        // When
        List<HashTag> result = hashTagService.findOrCreateHashTags(hashTagNames);

        // Then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(existingHashTagName, result.get(0).getName());
        assertEquals(existingHashTagName, result.get(1).getName());

        verify(hashTagRepository, times(2)).findByName(existingHashTagName);
        verify(hashTagRepository, never()).save(any(HashTag.class));
    }

    @Test
    @DisplayName("많은 해시태그를 한번에 처리하는 테스트")
    void findOrCreateHashTagsWithManyTags() {
        // Given
        List<String> manyHashTagNames = Arrays.asList("태그1", "태그2", "태그3", "태그4", "태그5");
        when(hashTagRepository.findByName(anyString())).thenReturn(Optional.empty());
        when(hashTagRepository.save(any(HashTag.class))).thenAnswer(invocation -> {
            HashTag arg = invocation.getArgument(0);
            return arg;
        });

        // When
        List<HashTag> result = hashTagService.findOrCreateHashTags(manyHashTagNames);

        // Then
        assertNotNull(result);
        assertEquals(5, result.size());

        verify(hashTagRepository, times(5)).findByName(anyString());
        verify(hashTagRepository, times(5)).save(any(HashTag.class));
    }
}