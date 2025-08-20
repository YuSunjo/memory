package com.memory.service.hashTag;

import com.memory.domain.hashtag.HashTag;
import com.memory.domain.hashtag.MemoryHashTag;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoryHashTagServiceTest {

    @Mock
    private MemoryHashTagRepository memoryHashTagRepository;

    @Mock
    private HashTagService hashTagService;

    @InjectMocks
    private MemoryHashTagService memoryHashTagService;

    private Memory memory;
    private HashTag hashTag1;
    private HashTag hashTag2;
    private MemoryHashTag memoryHashTag1;
    private MemoryHashTag memoryHashTag2;

    @BeforeEach
    void setUp() {
        memory = mock(Memory.class);
        
        hashTag1 = HashTag.create("해시태그1");
        setId(hashTag1, 1L);
        
        hashTag2 = HashTag.create("해시태그2");
        setId(hashTag2, 2L);

        memoryHashTag1 = new MemoryHashTag(memory, hashTag1);
        setId(memoryHashTag1, 1L);
        
        memoryHashTag2 = new MemoryHashTag(memory, hashTag2);
        setId(memoryHashTag2, 2L);
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
    @DisplayName("메모리 해시태그 생성 성공 테스트")
    void createMemoryHashTagsSuccess() {
        // Given
        List<HashTag> hashTags = Arrays.asList(hashTag1, hashTag2);
        List<MemoryHashTag> memoryHashTags = Arrays.asList(memoryHashTag1, memoryHashTag2);
        when(memoryHashTagRepository.saveAll(anyList())).thenReturn(memoryHashTags);

        // When
        assertDoesNotThrow(() -> memoryHashTagService.createMemoryHashTags(memory, hashTags));

        // Then
        verify(memoryHashTagRepository).saveAll(anyList());
        verify(memory).addMemoryHashTags(anyList());
    }

    @Test
    @DisplayName("메모리 해시태그 생성 테스트 - null 해시태그")
    void createMemoryHashTagsWithNullHashTags() {
        // When
        assertDoesNotThrow(() -> memoryHashTagService.createMemoryHashTags(memory, null));

        // Then
        verify(memoryHashTagRepository, never()).saveAll(anyList());
        verify(memory, never()).addMemoryHashTags(anyList());
    }

    @Test
    @DisplayName("메모리 해시태그 생성 테스트 - 빈 해시태그 리스트")
    void createMemoryHashTagsWithEmptyHashTags() {
        // When
        assertDoesNotThrow(() -> memoryHashTagService.createMemoryHashTags(memory, Collections.emptyList()));

        // Then
        verify(memoryHashTagRepository, never()).saveAll(anyList());
        verify(memory, never()).addMemoryHashTags(anyList());
    }

    @Test
    @DisplayName("메모리 해시태그 생성 테스트 - 단일 해시태그")
    void createMemoryHashTagsWithSingleHashTag() {
        // Given
        List<HashTag> hashTags = List.of(hashTag1);
        List<MemoryHashTag> memoryHashTags = List.of(memoryHashTag1);
        when(memoryHashTagRepository.saveAll(anyList())).thenReturn(memoryHashTags);

        // When
        assertDoesNotThrow(() -> memoryHashTagService.createMemoryHashTags(memory, hashTags));

        // Then
        verify(memoryHashTagRepository).saveAll(anyList());
        verify(memory).addMemoryHashTags(anyList());
    }

    @Test
    @DisplayName("메모리 해시태그 업데이트 성공 테스트")
    void updateMemoryHashTagsSuccess() {
        // Given
        List<HashTag> newHashTags = Arrays.asList(hashTag1, hashTag2);
        List<MemoryHashTag> newMemoryHashTags = Arrays.asList(memoryHashTag1, memoryHashTag2);
        when(memoryHashTagRepository.saveAll(anyList())).thenReturn(newMemoryHashTags);

        // When
        assertDoesNotThrow(() -> memoryHashTagService.updateMemoryHashTags(memory, newHashTags));

        // Then
        verify(hashTagService).decrementUseCountForMemoryHashTags(memory);
        verify(memoryHashTagRepository).deleteByMemory(memory);
        verify(memory).clearHashTags();
        verify(memoryHashTagRepository).saveAll(anyList());
        verify(memory).addMemoryHashTags(anyList());
    }

    @Test
    @DisplayName("메모리 해시태그 업데이트 테스트 - null 새 해시태그")
    void updateMemoryHashTagsWithNullNewHashTags() {
        // When
        assertDoesNotThrow(() -> memoryHashTagService.updateMemoryHashTags(memory, null));

        // Then
        verify(hashTagService).decrementUseCountForMemoryHashTags(memory);
        verify(memoryHashTagRepository).deleteByMemory(memory);
        verify(memory).clearHashTags();
        verify(memoryHashTagRepository, never()).saveAll(anyList());
        verify(memory, never()).addMemoryHashTags(anyList());
    }

    @Test
    @DisplayName("메모리 해시태그 업데이트 테스트 - 빈 새 해시태그 리스트")
    void updateMemoryHashTagsWithEmptyNewHashTags() {
        // When
        assertDoesNotThrow(() -> memoryHashTagService.updateMemoryHashTags(memory, Collections.emptyList()));

        // Then
        verify(hashTagService).decrementUseCountForMemoryHashTags(memory);
        verify(memoryHashTagRepository).deleteByMemory(memory);
        verify(memory).clearHashTags();
        verify(memoryHashTagRepository, never()).saveAll(anyList());
        verify(memory, never()).addMemoryHashTags(anyList());
    }

    @Test
    @DisplayName("메모리 해시태그 업데이트 테스트 - 기존과 동일한 해시태그")
    void updateMemoryHashTagsWithSameHashTags() {
        // Given
        List<HashTag> sameHashTags = Arrays.asList(hashTag1, hashTag2);
        List<MemoryHashTag> newMemoryHashTags = Arrays.asList(memoryHashTag1, memoryHashTag2);
        when(memoryHashTagRepository.saveAll(anyList())).thenReturn(newMemoryHashTags);

        // When
        assertDoesNotThrow(() -> memoryHashTagService.updateMemoryHashTags(memory, sameHashTags));

        // Then
        verify(hashTagService).decrementUseCountForMemoryHashTags(memory);
        verify(memoryHashTagRepository).deleteByMemory(memory);
        verify(memory).clearHashTags();
        verify(memoryHashTagRepository).saveAll(anyList());
        verify(memory).addMemoryHashTags(anyList());
    }

    @Test
    @DisplayName("메모리 해시태그 업데이트 테스트 - 단일 해시태그로 업데이트")
    void updateMemoryHashTagsToSingleHashTag() {
        // Given
        List<HashTag> singleHashTag = List.of(hashTag1);
        List<MemoryHashTag> newMemoryHashTags = List.of(memoryHashTag1);
        when(memoryHashTagRepository.saveAll(anyList())).thenReturn(newMemoryHashTags);

        // When
        assertDoesNotThrow(() -> memoryHashTagService.updateMemoryHashTags(memory, singleHashTag));

        // Then
        verify(hashTagService).decrementUseCountForMemoryHashTags(memory);
        verify(memoryHashTagRepository).deleteByMemory(memory);
        verify(memory).clearHashTags();
        verify(memoryHashTagRepository).saveAll(anyList());
        verify(memory).addMemoryHashTags(anyList());
    }

    @Test
    @DisplayName("메모리 해시태그 업데이트 테스트 - 더 많은 해시태그로 업데이트")
    void updateMemoryHashTagsToMoreHashTags() {
        // Given
        HashTag hashTag3 = HashTag.create("해시태그3");
        setId(hashTag3, 3L);
        
        List<HashTag> moreHashTags = Arrays.asList(hashTag1, hashTag2, hashTag3);
        when(memoryHashTagRepository.saveAll(anyList())).thenReturn(Collections.emptyList());

        // When
        assertDoesNotThrow(() -> memoryHashTagService.updateMemoryHashTags(memory, moreHashTags));

        // Then
        verify(hashTagService).decrementUseCountForMemoryHashTags(memory);
        verify(memoryHashTagRepository).deleteByMemory(memory);
        verify(memory).clearHashTags();
        verify(memoryHashTagRepository).saveAll(anyList());
        verify(memory).addMemoryHashTags(anyList());
    }

    @Test
    @DisplayName("메모리 해시태그 생성과 업데이트 연속 호출 테스트")
    void createThenUpdateMemoryHashTags() {
        // Given
        List<HashTag> initialHashTags = List.of(hashTag1);
        List<HashTag> updatedHashTags = Arrays.asList(hashTag1, hashTag2);
        List<MemoryHashTag> initialMemoryHashTags = List.of(memoryHashTag1);
        List<MemoryHashTag> updatedMemoryHashTags = Arrays.asList(memoryHashTag1, memoryHashTag2);
        
        when(memoryHashTagRepository.saveAll(anyList()))
                .thenReturn(initialMemoryHashTags)
                .thenReturn(updatedMemoryHashTags);

        // When
        assertDoesNotThrow(() -> {
            memoryHashTagService.createMemoryHashTags(memory, initialHashTags);
            memoryHashTagService.updateMemoryHashTags(memory, updatedHashTags);
        });

        // Then
        verify(memoryHashTagRepository, times(2)).saveAll(anyList());
        verify(memory, times(2)).addMemoryHashTags(anyList());
        verify(hashTagService).decrementUseCountForMemoryHashTags(memory);
        verify(memoryHashTagRepository).deleteByMemory(memory);
        verify(memory).clearHashTags();
    }

    @Test
    @DisplayName("메모리 해시태그 업데이트 시 예외 처리 테스트")
    void updateMemoryHashTagsErrorHandling() {
        // Given
        List<HashTag> newHashTags = Arrays.asList(hashTag1, hashTag2);
        doThrow(new RuntimeException("Database error")).when(memoryHashTagRepository).deleteByMemory(memory);

        // When & Then
        assertThrows(RuntimeException.class, () -> 
            memoryHashTagService.updateMemoryHashTags(memory, newHashTags));

        verify(hashTagService).decrementUseCountForMemoryHashTags(memory);
        verify(memoryHashTagRepository).deleteByMemory(memory);
    }
}