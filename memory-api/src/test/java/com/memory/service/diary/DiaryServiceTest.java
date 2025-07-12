package com.memory.service.diary;

import com.memory.domain.diary.Diary;
import com.memory.domain.diary.repository.DiaryRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.diary.DiaryRequest;
import com.memory.dto.diary.response.DiaryResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
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
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiaryServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private DiaryRepository diaryRepository;

    @InjectMocks
    private DiaryService diaryService;

    private Member member;
    private Diary diary;
    private DiaryRequest.Create createRequest;
    private DiaryRequest.Update updateRequest;

    private final Long memberId = 1L;
    private final Long diaryId = 1L;
    private final String title = "테스트 다이어리";
    private final String content = "오늘은 좋은 하루였다.";
    private final LocalDate date = LocalDate.of(2025, 7, 12);
    private final String mood = "HAPPY";
    private final String weather = "SUNNY";

    @BeforeEach
    void setUp() {
        // Member 객체 생성
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        // Diary 객체 생성
        diary = Diary.builder()
                .member(member)
                .title(title)
                .content(content)
                .date(date)
                .mood(mood)
                .weather(weather)
                .build();
        setId(diary, diaryId);

        // Request 객체들 생성
        createRequest = createDiaryCreateRequest();
        updateRequest = createDiaryUpdateRequest();
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

    private DiaryRequest.Create createDiaryCreateRequest() {
        try {
            return new DiaryRequest.Create("테스트 다이어리", "오늘은 좋은 하루였다.", date, mood, weather);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request", e);
        }
    }

    private DiaryRequest.Update createDiaryUpdateRequest() {
        try {
            return new DiaryRequest.Update("수정된 다이어리", "수정된 내용", date.plusDays(1), "EXCITED", "CLOUDY");
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request", e);
        }
    }

    @Test
    @DisplayName("다이어리 생성 성공 테스트")
    void createDiarySuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(diaryRepository.save(any(Diary.class))).thenReturn(diary);

        // When
        DiaryResponse response = diaryService.createDiary(memberId, createRequest);

        // Then
        assertNotNull(response);
        assertEquals(diaryId, response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(content, response.getContent());
        assertEquals(date, response.getDate());
        assertEquals(mood, response.getMood());
        assertEquals(weather, response.getWeather());

        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository).save(any(Diary.class));
    }

    @Test
    @DisplayName("다이어리 생성 실패 테스트 - 존재하지 않는 회원")
    void createDiaryFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> diaryService.createDiary(memberId, createRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository, never()).save(any(Diary.class));
    }

    @Test
    @DisplayName("다이어리 수정 성공 테스트")
    void updateDiarySuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(diary));

        // When
        DiaryResponse response = diaryService.updateDiary(memberId, diaryId, updateRequest);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository).findById(diaryId);
    }

    @Test
    @DisplayName("다이어리 수정 실패 테스트 - 존재하지 않는 회원")
    void updateDiaryFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> diaryService.updateDiary(memberId, diaryId, updateRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("다이어리 수정 실패 테스트 - 존재하지 않는 다이어리")
    void updateDiaryFailDiaryNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(diaryRepository.findById(diaryId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> diaryService.updateDiary(memberId, diaryId, updateRequest));

        assertEquals("다이어리를 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository).findById(diaryId);
    }

    @Test
    @DisplayName("다이어리 수정 실패 테스트 - 권한 없음")
    void updateDiaryFailNoPermission() {
        // Given
        Diary otherDiary = spy(diary);
        when(otherDiary.isOwner(member)).thenReturn(true); // isOwner가 true면 권한 없음
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(otherDiary));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> diaryService.updateDiary(memberId, diaryId, updateRequest));

        assertEquals("해당 다이어리에 대한 권한이 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository).findById(diaryId);
    }

    @Test
    @DisplayName("다이어리 삭제 성공 테스트")
    void deleteDiarySuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(diary));

        // When
        assertDoesNotThrow(() -> diaryService.deleteDiary(memberId, diaryId));

        // Then
        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository).findById(diaryId);
    }

    @Test
    @DisplayName("다이어리 삭제 실패 테스트 - 존재하지 않는 회원")
    void deleteDiaryFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> diaryService.deleteDiary(memberId, diaryId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("다이어리 삭제 실패 테스트 - 존재하지 않는 다이어리")
    void deleteDiaryFailDiaryNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(diaryRepository.findById(diaryId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> diaryService.deleteDiary(memberId, diaryId));

        assertEquals("다이어리를 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository).findById(diaryId);
    }

    @Test
    @DisplayName("다이어리 삭제 실패 테스트 - 권한 없음")
    void deleteDiaryFailNoPermission() {
        // Given
        Diary otherDiary = spy(diary);
        when(otherDiary.isOwner(member)).thenReturn(true);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(diaryRepository.findById(diaryId)).thenReturn(Optional.of(otherDiary));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> diaryService.deleteDiary(memberId, diaryId));

        assertEquals("해당 다이어리에 대한 권한이 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository).findById(diaryId);
    }

    @Test
    @DisplayName("날짜 범위로 다이어리 목록 조회 성공 테스트")
    void getDiariesByDateRangeSuccess() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);

        Diary diary2 = Diary.builder()
                .member(member)
                .title("두 번째 다이어리")
                .content("두 번째 내용")
                .date(date.plusDays(1))
                .mood(mood)
                .weather(weather)
                .build();
        setId(diary2, 2L);

        List<Diary> diaries = Arrays.asList(diary, diary2);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(diaryRepository.findActiveDiariesByMemberAndDateBetween(member, startDate, endDate))
                .thenReturn(diaries);

        // When
        List<DiaryResponse> responses = diaryService.getDiariesByDateRange(memberId, startDate, endDate);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(diaryId, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());
        assertEquals(title, responses.get(0).getTitle());
        assertEquals("두 번째 다이어리", responses.get(1).getTitle());

        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository).findActiveDiariesByMemberAndDateBetween(member, startDate, endDate);
    }

    @Test
    @DisplayName("날짜 범위로 다이어리 목록 조회 실패 테스트 - 존재하지 않는 회원")
    void getDiariesByDateRangeFailMemberNotFound() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 7, 1);
        LocalDate endDate = LocalDate.of(2025, 7, 31);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> diaryService.getDiariesByDateRange(memberId, startDate, endDate));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository, never()).findActiveDiariesByMemberAndDateBetween(any(), any(), any());
    }

    @Test
    @DisplayName("날짜 범위로 다이어리 목록 조회 테스트 - 빈 결과")
    void getDiariesByDateRangeEmptyResult() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 8, 1);
        LocalDate endDate = LocalDate.of(2025, 8, 31);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(diaryRepository.findActiveDiariesByMemberAndDateBetween(member, startDate, endDate))
                .thenReturn(List.of());

        // When
        List<DiaryResponse> responses = diaryService.getDiariesByDateRange(memberId, startDate, endDate);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(memberRepository).findMemberById(memberId);
        verify(diaryRepository).findActiveDiariesByMemberAndDateBetween(member, startDate, endDate);
    }
}
