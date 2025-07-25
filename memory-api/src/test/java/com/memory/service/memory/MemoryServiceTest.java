package com.memory.service.memory;

import com.memory.domain.file.repository.FileRepository;
import com.memory.domain.map.Map;
import com.memory.domain.map.repository.MapRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.memory.Memory;
import com.memory.domain.memory.MemoryType;
import com.memory.domain.memory.repository.MemoryRepository;
import com.memory.dto.memory.MemoryRequest;
import com.memory.dto.memory.response.MemoryResponse;
import com.memory.exception.customException.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.memory.domain.file.FileType.MEMORY;
import static com.memory.domain.map.MapType.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemoryServiceTest {

    @Mock
    private MemoryRepository memoryRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MapRepository mapRepository;

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private MemoryService memoryService;

    private Member member;
    private Map mapEntity;
    private Memory memory;
    private com.memory.domain.file.File file1, file2;
    private MemoryRequest.Create createRequest;
    private MemoryRequest.Update updateRequest;

    private final Long memberId = 1L;
    private final Long mapId = 1L;
    private final Long memoryId = 1L;
    private final Long fileId1 = 1L;
    private final Long fileId2 = 2L;
    private final String title = "테스트 메모리";
    private final String content = "테스트 내용";
    private final String locationName = "테스트 장소";
    private final LocalDate memorableDate = LocalDate.of(2023, 12, 25);

    @BeforeEach
    void setUp() {
        // Member 객체 생성
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        // Map 객체 생성
        mapEntity = com.memory.domain.map.Map.builder()
                .name("테스트 맵")
                .description("테스트 맵 설명")
                .address("서울특별시 중구 명동")
                .latitude("37.5665")
                .longitude("126.9780")
                .mapType(USER_PLACE)
                .member(member)
                .build();
        setId(mapEntity, mapId);

        // File 객체들 생성
        file1 = com.memory.domain.file.File.builder()
                .originalFileName("test1.jpg")
                .fileName("stored_test1.jpg")
                .fileUrl("https://example.com/test1.jpg")
                .fileType(MEMORY)
                .fileSize(1024L)
                .build();
        setId(file1, fileId1);

        file2 = com.memory.domain.file.File.builder()
                .originalFileName("test2.jpg")
                .fileName("stored_test2.jpg")
                .fileUrl("https://example.com/test2.jpg")
                .fileType(MEMORY)
                .fileSize(2048L)
                .build();
        setId(file2, fileId2);

        // Memory 객체 생성
        memory = new Memory(title, content, locationName, memorableDate, MemoryType.PRIVATE, member, mapEntity);
        setId(memory, memoryId);

        // Request 객체들 생성
        createRequest = createMemoryCreateRequest();
        updateRequest = createMemoryUpdateRequest();
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

    private MemoryRequest.Create createMemoryCreateRequest() {
        return new MemoryRequest.Create(title, content, locationName, memorableDate, mapId, MemoryType.PRIVATE, 
                Arrays.asList(fileId1, fileId2), Arrays.asList("테스트", "메모리"));
    }

    private MemoryRequest.Update createMemoryUpdateRequest() {
        return new MemoryRequest.Update("수정된 메모리", "수정된 내용", "수정된 장소", 
                LocalDate.of(2023, 12, 26), MemoryType.PUBLIC, List.of(fileId1), Arrays.asList("수정", "업데이트"));
    }

    @Test
    @DisplayName("메모리 생성 성공 테스트")
    void createMemorySuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(mapRepository.findById(mapId)).thenReturn(Optional.of(mapEntity));
        when(memoryRepository.save(any(Memory.class))).thenReturn(memory);
        when(fileRepository.findAllById(Arrays.asList(fileId1, fileId2))).thenReturn(Arrays.asList(file1, file2));

        // When
        MemoryResponse response = memoryService.createMemory(memberId, createRequest);

        // Then
        assertNotNull(response);
        assertEquals(memoryId, response.id());
        assertEquals(title, response.title());
        assertEquals(content, response.content());
        assertEquals(locationName, response.locationName());
        assertEquals(MemoryType.PRIVATE, response.memoryType());

        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository).findById(mapId);
        verify(memoryRepository).save(any(Memory.class));
        verify(fileRepository).findAllById(Arrays.asList(fileId1, fileId2));
    }

    @Test
    @DisplayName("메모리 생성 실패 테스트 - 존재하지 않는 회원")
    void createMemoryFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.createMemory(memberId, createRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository, never()).findById(anyLong());
        verify(memoryRepository, never()).save(any(Memory.class));
    }

    @Test
    @DisplayName("메모리 생성 실패 테스트 - 존재하지 않는 지도")
    void createMemoryFailMapNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(mapRepository.findById(mapId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.createMemory(memberId, createRequest));

        assertEquals("지도를 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository).findById(mapId);
        verify(memoryRepository, never()).save(any(Memory.class));
    }

    @Test
    @DisplayName("메모리 생성 테스트 - 파일 없이")
    void createMemoryWithoutFiles() {
        // Given
        MemoryRequest.Create requestWithoutFiles = new MemoryRequest.Create(title, content, locationName, 
                memorableDate, mapId, MemoryType.PRIVATE, null, Arrays.asList("파일없음", "테스트"));

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(mapRepository.findById(mapId)).thenReturn(Optional.of(mapEntity));
        when(memoryRepository.save(any(Memory.class))).thenReturn(memory);

        // When
        MemoryResponse response = memoryService.createMemory(memberId, requestWithoutFiles);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository).findById(mapId);
        verify(memoryRepository).save(any(Memory.class));
        verify(fileRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("메모리 조회 성공 테스트")
    void findMemoryByIdSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.of(memory));

        // When
        MemoryResponse response = memoryService.findMemoryById(memberId, memoryId);

        // Then
        assertNotNull(response);
        assertEquals(memoryId, response.id());
        assertEquals(title, response.title());
        assertEquals(content, response.content());

        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
    }

    @Test
    @DisplayName("메모리 조회 실패 테스트 - 존재하지 않는 회원")
    void findMemoryByIdFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.findMemoryById(memberId, memoryId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository, never()).findMemoryByIdAndMemberId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("메모리 조회 실패 테스트 - 존재하지 않는 메모리")
    void findMemoryByIdFailMemoryNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.findMemoryById(memberId, memoryId));

        assertEquals("해당 유저의 메모리를 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
    }

    @Test
    @DisplayName("회원별 메모리 목록 조회 성공 테스트 - 첫 페이지")
    void findMemoriesByMemberSuccessFirstPage() {
        // Given
        int size = 10;
        Memory memory2 = new Memory("두 번째 메모리", "두 번째 내용", "두 번째 장소", 
                LocalDate.of(2023, 12, 27), MemoryType.PRIVATE, member, mapEntity);
        setId(memory2, 2L);

        List<Memory> memories = Arrays.asList(memory, memory2);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findByMemberAndMemoryType(member, MemoryType.PRIVATE, size))
                .thenReturn(memories);

        // When
        List<MemoryResponse> responses = memoryService.findMemoriesByMember(memberId, null, size, MemoryType.PRIVATE);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(memoryId, responses.get(0).id());
        assertEquals(2L, responses.get(1).id());

        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findByMemberAndMemoryType(member, MemoryType.PRIVATE, size);
    }

    @Test
    @DisplayName("회원별 메모리 목록 조회 성공 테스트 - 다음 페이지")
    void findMemoriesByMemberSuccessNextPage() {
        // Given
        Long lastMemoryId = 5L;
        int size = 10;
        List<Memory> memories = Collections.singletonList(memory);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findByMemberAndMemoryType(member, MemoryType.PRIVATE, lastMemoryId, size))
                .thenReturn(memories);

        // When
        List<MemoryResponse> responses = memoryService.findMemoriesByMember(memberId, lastMemoryId, size, MemoryType.PRIVATE);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(memoryId, responses.get(0).id());

        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findByMemberAndMemoryType(member, MemoryType.PRIVATE, lastMemoryId, size);
    }

    @Test
    @DisplayName("회원별 메모리 목록 조회 실패 테스트 - 존재하지 않는 회원")
    void findMemoriesByMemberFailMemberNotFound() {
        // Given
        Integer size = 10;
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.findMemoriesByMember(memberId, null, size, MemoryType.PRIVATE));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository, never()).findByMemberAndMemoryType(any(), any(), anyInt());
    }

    @Test
    @DisplayName("메모리 수정 성공 테스트")
    void updateMemorySuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.of(memory));
        when(fileRepository.findAllById(List.of(fileId1))).thenReturn(Collections.singletonList(file1));

        // When
        MemoryResponse response = memoryService.updateMemory(memberId, memoryId, updateRequest);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
        verify(fileRepository).findAllById(List.of(fileId1));
    }

    @Test
    @DisplayName("메모리 수정 실패 테스트 - 존재하지 않는 회원")
    void updateMemoryFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.updateMemory(memberId, memoryId, updateRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository, never()).findMemoryByIdAndMemberId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("메모리 수정 실패 테스트 - 존재하지 않는 메모리")
    void updateMemoryFailMemoryNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.updateMemory(memberId, memoryId, updateRequest));

        assertEquals("해당 유저의 메모리를 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
    }

    @Test
    @DisplayName("메모리 수정 실패 테스트 - 권한 없음")
    void updateMemoryFailNoPermission() {
        // Given
        Member otherMember = new Member("다른 사용자", "otheruser", "other@example.com", "encodedPassword");
        setId(otherMember, 2L);
        
        Memory otherMemory = new Memory("다른 메모리", "다른 내용", "다른 장소", 
                LocalDate.of(2023, 12, 28), MemoryType.PRIVATE, otherMember, mapEntity);
        setId(otherMemory, memoryId);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.of(otherMemory));

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.updateMemory(memberId, memoryId, updateRequest));

        assertEquals("해당 메모리에 접근 권한이 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
    }

    @Test
    @DisplayName("메모리 수정 테스트 - 파일 없이")
    void updateMemoryWithoutFiles() {
        // Given
        MemoryRequest.Update updateWithoutFiles = new MemoryRequest.Update("파일 없는 수정", "파일 없는 내용", 
                "파일 없는 장소", LocalDate.of(2023, 12, 29), MemoryType.PUBLIC, null, Arrays.asList("파일없음", "수정"));

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.of(memory));

        // When
        MemoryResponse response = memoryService.updateMemory(memberId, memoryId, updateWithoutFiles);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
        verify(fileRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("메모리 삭제 성공 테스트")
    void deleteMemorySuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.of(memory));

        // When
        assertDoesNotThrow(() -> memoryService.deleteMemory(memberId, memoryId));

        // Then
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
    }

    @Test
    @DisplayName("메모리 삭제 실패 테스트 - 존재하지 않는 회원")
    void deleteMemoryFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.deleteMemory(memberId, memoryId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository, never()).findMemoryByIdAndMemberId(anyLong(), anyLong());
    }

    @Test
    @DisplayName("메모리 삭제 실패 테스트 - 존재하지 않는 메모리")
    void deleteMemoryFailMemoryNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.deleteMemory(memberId, memoryId));

        assertEquals("해당 유저의 메모리를 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
    }

    @Test
    @DisplayName("메모리 삭제 실패 테스트 - 권한 없음")
    void deleteMemoryFailNoPermission() {
        // Given
        Member otherMember = new Member("다른 사용자", "otheruser", "other@example.com", "encodedPassword");
        setId(otherMember, 2L);
        
        Memory otherMemory = new Memory("다른 메모리", "다른 내용", "다른 장소", 
                LocalDate.of(2023, 12, 30), MemoryType.PRIVATE, otherMember, mapEntity);
        setId(otherMemory, memoryId);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.of(otherMemory));

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> memoryService.deleteMemory(memberId, memoryId));

        assertEquals("해당 메모리에 접근 권한이 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
    }

    @Test
    @DisplayName("공개 메모리 목록 조회 성공 테스트 - 첫 페이지")
    void findPublicMemoriesSuccessFirstPage() {
        // Given
        int size = 10;
        Memory publicMemory = new Memory("공개 메모리", "공개 내용", "공개 장소", 
                LocalDate.of(2024, 1, 1), MemoryType.PUBLIC, member, mapEntity);
        setId(publicMemory, 3L);

        List<Memory> publicMemories = List.of(publicMemory);

        when(memoryRepository.findByMemoryType(MemoryType.PUBLIC, size)).thenReturn(publicMemories);

        // When
        List<MemoryResponse> responses = memoryService.findPublicMemories(null, size);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(3L, responses.get(0).id());
        assertEquals("공개 메모리", responses.get(0).title());
        assertEquals(MemoryType.PUBLIC, responses.get(0).memoryType());

        verify(memoryRepository).findByMemoryType(MemoryType.PUBLIC, size);
    }

    @Test
    @DisplayName("공개 메모리 목록 조회 성공 테스트 - 다음 페이지")
    void findPublicMemoriesSuccessNextPage() {
        // Given
        Long lastMemoryId = 5L;
        int size = 10;
        List<Memory> publicMemories = List.of();

        when(memoryRepository.findByMemoryType(MemoryType.PUBLIC, lastMemoryId, size)).thenReturn(publicMemories);

        // When
        List<MemoryResponse> responses = memoryService.findPublicMemories(lastMemoryId, size);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(memoryRepository).findByMemoryType(MemoryType.PUBLIC, lastMemoryId, size);
    }

    @Test
    @DisplayName("공개 메모리 목록 조회 테스트 - size가 null인 경우")
    void findPublicMemoriesWithNullSize() {
        // Given
        Long lastMemoryId = 5L;
        List<Memory> publicMemories = List.of();

        when(memoryRepository.findByMemoryType(MemoryType.PUBLIC, lastMemoryId, 10)).thenReturn(publicMemories);

        // When
        List<MemoryResponse> responses = memoryService.findPublicMemories(lastMemoryId, null);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(memoryRepository).findByMemoryType(MemoryType.PUBLIC, lastMemoryId, 10);
    }

    @Test
    @DisplayName("다양한 메모리 타입으로 조회 테스트")
    void findMemoriesByMemberWithDifferentTypes() {
        // Given
        int size = 10;
        Memory publicMemory = new Memory("공개 메모리", "공개 내용", "공개 장소", 
                LocalDate.of(2024, 1, 2), MemoryType.PUBLIC, member, mapEntity);
        setId(publicMemory, 3L);

        List<Memory> publicMemories = List.of(publicMemory);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findByMemberAndMemoryType(member, MemoryType.PUBLIC, size))
                .thenReturn(publicMemories);

        // When
        List<MemoryResponse> responses = memoryService.findMemoriesByMember(memberId, null, size, MemoryType.PUBLIC);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals(3L, responses.get(0).id());
        assertEquals(MemoryType.PUBLIC, responses.get(0).memoryType());

        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findByMemberAndMemoryType(member, MemoryType.PUBLIC, size);
    }

    @Test
    @DisplayName("빈 파일 리스트로 메모리 생성 테스트")
    void createMemoryWithEmptyFileList() {
        // Given
        MemoryRequest.Create requestWithEmptyFiles = new MemoryRequest.Create("빈 파일 메모리", "빈 파일 내용",
                "빈 파일 장소", LocalDate.of(2024, 1, 3), mapId, MemoryType.PRIVATE, List.of(), Arrays.asList("빈파일", "테스트"));

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(mapRepository.findById(mapId)).thenReturn(Optional.of(mapEntity));
        when(memoryRepository.save(any(Memory.class))).thenReturn(memory);

        // When
        MemoryResponse response = memoryService.createMemory(memberId, requestWithEmptyFiles);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository).findById(mapId);
        verify(memoryRepository).save(any(Memory.class));
        verify(fileRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("빈 파일 리스트로 메모리 수정 테스트")
    void updateMemoryWithEmptyFileList() {
        // Given
        MemoryRequest.Update updateWithEmptyFiles = new MemoryRequest.Update("빈 파일 수정", "빈 파일 내용", 
                "빈 파일 장소", LocalDate.of(2024, 1, 4), MemoryType.PUBLIC, List.of(), Arrays.asList("빈파일", "수정"));

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.of(memory));

        // When
        MemoryResponse response = memoryService.updateMemory(memberId, memoryId, updateWithEmptyFiles);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
        verify(fileRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("해시태그만 있는 메모리 생성 테스트")
    void createMemoryWithHashTagsOnly() {
        // Given
        MemoryRequest.Create requestWithHashTagsOnly = new MemoryRequest.Create("해시태그 메모리", "해시태그 내용",
                "해시태그 장소", LocalDate.of(2024, 1, 5), mapId, MemoryType.PUBLIC, null, Arrays.asList("여행", "카페", "데이트"));

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(mapRepository.findById(mapId)).thenReturn(Optional.of(mapEntity));
        when(memoryRepository.save(any(Memory.class))).thenReturn(memory);

        // When
        MemoryResponse response = memoryService.createMemory(memberId, requestWithHashTagsOnly);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository).findById(mapId);
        verify(memoryRepository).save(any(Memory.class));
        verify(fileRepository, never()).findAllById(any());
    }

    @Test
    @DisplayName("빈 해시태그 리스트로 메모리 생성 테스트")
    void createMemoryWithEmptyHashTagList() {
        // Given
        MemoryRequest.Create requestWithEmptyHashTags = new MemoryRequest.Create("빈 해시태그 메모리", "빈 해시태그 내용",
                "빈 해시태그 장소", LocalDate.of(2024, 1, 6), mapId, MemoryType.PRIVATE, List.of(fileId1), List.of());

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(mapRepository.findById(mapId)).thenReturn(Optional.of(mapEntity));
        when(memoryRepository.save(any(Memory.class))).thenReturn(memory);
        when(fileRepository.findAllById(Arrays.asList(fileId1))).thenReturn(Arrays.asList(file1));

        // When
        MemoryResponse response = memoryService.createMemory(memberId, requestWithEmptyHashTags);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository).findById(mapId);
        verify(memoryRepository).save(any(Memory.class));
        verify(fileRepository).findAllById(Arrays.asList(fileId1));
    }

    @Test
    @DisplayName("많은 해시태그가 있는 메모리 생성 테스트")
    void createMemoryWithManyHashTags() {
        // Given
        List<String> manyHashTags = Arrays.asList("여행", "카페", "데이트", "서울", "맛집", "친구", "주말", "힐링");
        MemoryRequest.Create requestWithManyHashTags = new MemoryRequest.Create("많은 해시태그 메모리", "많은 해시태그 내용",
                "많은 해시태그 장소", LocalDate.of(2024, 1, 7), mapId, MemoryType.PUBLIC, Arrays.asList(fileId1, fileId2), manyHashTags);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(mapRepository.findById(mapId)).thenReturn(Optional.of(mapEntity));
        when(memoryRepository.save(any(Memory.class))).thenReturn(memory);
        when(fileRepository.findAllById(Arrays.asList(fileId1, fileId2))).thenReturn(Arrays.asList(file1, file2));

        // When
        MemoryResponse response = memoryService.createMemory(memberId, requestWithManyHashTags);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(mapRepository).findById(mapId);
        verify(memoryRepository).save(any(Memory.class));
        verify(fileRepository).findAllById(Arrays.asList(fileId1, fileId2));
    }

    @Test
    @DisplayName("해시태그 업데이트 테스트")
    void updateMemoryWithDifferentHashTags() {
        // Given
        MemoryRequest.Update updateWithNewHashTags = new MemoryRequest.Update("업데이트된 메모리", "업데이트된 내용", 
                "업데이트된 장소", LocalDate.of(2024, 1, 8), MemoryType.PRIVATE, Arrays.asList(fileId2), Arrays.asList("새로운", "해시태그", "업데이트"));

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.of(memory));
        when(fileRepository.findAllById(Arrays.asList(fileId2))).thenReturn(Arrays.asList(file2));

        // When
        MemoryResponse response = memoryService.updateMemory(memberId, memoryId, updateWithNewHashTags);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
        verify(fileRepository).findAllById(Arrays.asList(fileId2));
    }

    @Test
    @DisplayName("해시태그 제거 테스트")
    void updateMemoryToRemoveHashTags() {
        // Given
        MemoryRequest.Update updateWithoutHashTags = new MemoryRequest.Update("해시태그 제거", "해시태그 제거 내용", 
                "해시태그 제거 장소", LocalDate.of(2024, 1, 9), MemoryType.PUBLIC, null, null);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryByIdAndMemberId(memoryId, memberId)).thenReturn(Optional.of(memory));

        // When
        MemoryResponse response = memoryService.updateMemory(memberId, memoryId, updateWithoutHashTags);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryByIdAndMemberId(memoryId, memberId);
        verify(fileRepository, never()).findAllById(any());
    }
}
