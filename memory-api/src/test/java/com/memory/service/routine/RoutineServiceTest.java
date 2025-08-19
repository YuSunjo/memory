package com.memory.service.routine;

import com.memory.domain.common.repeat.RepeatSetting;
import com.memory.domain.common.repeat.RepeatType;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.routine.Routine;
import com.memory.domain.routine.repository.RoutineRepository;
import com.memory.domain.todo.Todo;
import com.memory.domain.todo.repository.TodoRepository;
import com.memory.dto.routine.RoutineRequest;
import com.memory.dto.routine.response.RoutinePreviewResponse;
import com.memory.dto.routine.response.RoutineResponse;
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
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoutineServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private RoutineService routineService;

    private Member member;
    private Routine routine;
    private Routine activeRoutine;
    private Routine inactiveRoutine;
    private RoutineRequest.Create createRequest;
    private RoutineRequest.Update updateRequest;

    private final Long memberId = 1L;
    private final Long routineId = 1L;
    private final String title = "테스트 루틴";
    private final String content = "테스트 루틴 내용";

    @BeforeEach
    void setUp() {
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        RepeatSetting repeatSetting = RepeatSetting.of(RepeatType.DAILY, 1, LocalDate.now(), LocalDate.now().plusDays(30));
        
        routine = Routine.create(title, content, member, repeatSetting);
        setId(routine, routineId);

        activeRoutine = Routine.create("활성 루틴", "활성 루틴 내용", member, repeatSetting);
        setId(activeRoutine, 2L);

        inactiveRoutine = Routine.create("비활성 루틴", "비활성 루틴 내용", member, repeatSetting);
        inactiveRoutine.deactivate();
        setId(inactiveRoutine, 3L);

        createRequest = createRoutineCreateRequest();
        updateRequest = createRoutineUpdateRequest();
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

    private RoutineRequest.Create createRoutineCreateRequest() {
        RoutineRequest.Create request = new RoutineRequest.Create();
        try {
            Field titleField = request.getClass().getDeclaredField("title");
            titleField.setAccessible(true);
            titleField.set(request, title);

            Field contentField = request.getClass().getDeclaredField("content");
            contentField.setAccessible(true);
            contentField.set(request, content);

            Field repeatTypeField = request.getClass().getDeclaredField("repeatType");
            repeatTypeField.setAccessible(true);
            repeatTypeField.set(request, RepeatType.DAILY);

            Field intervalField = request.getClass().getDeclaredField("interval");
            intervalField.setAccessible(true);
            intervalField.set(request, 1);

            Field startDateField = request.getClass().getDeclaredField("startDate");
            startDateField.setAccessible(true);
            startDateField.set(request, LocalDate.now());

            Field endDateField = request.getClass().getDeclaredField("endDate");
            endDateField.setAccessible(true);
            endDateField.set(request, LocalDate.now().plusDays(30));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create request", e);
        }
        return request;
    }

    private RoutineRequest.Update createRoutineUpdateRequest() {
        RoutineRequest.Update request = new RoutineRequest.Update();
        try {
            Field titleField = request.getClass().getDeclaredField("title");
            titleField.setAccessible(true);
            titleField.set(request, "수정된 루틴");

            Field contentField = request.getClass().getDeclaredField("content");
            contentField.setAccessible(true);
            contentField.set(request, "수정된 루틴 내용");

            Field repeatTypeField = request.getClass().getDeclaredField("repeatType");
            repeatTypeField.setAccessible(true);
            repeatTypeField.set(request, RepeatType.WEEKLY);

            Field intervalField = request.getClass().getDeclaredField("interval");
            intervalField.setAccessible(true);
            intervalField.set(request, 2);

            Field startDateField = request.getClass().getDeclaredField("startDate");
            startDateField.setAccessible(true);
            startDateField.set(request, LocalDate.now());

            Field endDateField = request.getClass().getDeclaredField("endDate");
            endDateField.setAccessible(true);
            endDateField.set(request, LocalDate.now().plusDays(60));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create update request", e);
        }
        return request;
    }

    @Test
    @DisplayName("루틴 생성 성공 테스트")
    void createRoutineSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.save(any(Routine.class))).thenReturn(routine);

        // When
        RoutineResponse response = routineService.createRoutine(memberId, createRequest);

        // Then
        assertNotNull(response);
        assertEquals(routineId, response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(content, response.getContent());

        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).save(any(Routine.class));
    }

    @Test
    @DisplayName("루틴 생성 실패 테스트 - 존재하지 않는 회원")
    void createRoutineFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.createRoutine(memberId, createRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository, never()).save(any(Routine.class));
    }

    @Test
    @DisplayName("루틴 수정 성공 테스트")
    void updateRoutineSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.of(routine));

        // When
        RoutineResponse response = routineService.updateRoutine(memberId, routineId, updateRequest);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
    }

    @Test
    @DisplayName("루틴 수정 실패 테스트 - 존재하지 않는 회원")
    void updateRoutineFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.updateRoutine(memberId, routineId, updateRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository, never()).findByIdAndMember(anyLong(), any(Member.class));
    }

    @Test
    @DisplayName("루틴 수정 실패 테스트 - 존재하지 않는 루틴")
    void updateRoutineFailRoutineNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.updateRoutine(memberId, routineId, updateRequest));

        assertEquals("루틴을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
    }

    @Test
    @DisplayName("루틴 삭제 성공 테스트")
    void deleteRoutineSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.of(routine));

        // When
        assertDoesNotThrow(() -> routineService.deleteRoutine(memberId, routineId));

        // Then
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
    }

    @Test
    @DisplayName("루틴 삭제 실패 테스트 - 존재하지 않는 회원")
    void deleteRoutineFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.deleteRoutine(memberId, routineId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository, never()).findByIdAndMember(anyLong(), any(Member.class));
    }

    @Test
    @DisplayName("루틴 삭제 실패 테스트 - 존재하지 않는 루틴")
    void deleteRoutineFailRoutineNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.deleteRoutine(memberId, routineId));

        assertEquals("루틴을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
    }

    @Test
    @DisplayName("루틴 활성화/비활성화 토글 성공 테스트 - 활성화된 루틴을 비활성화")
    void toggleRoutineActiveSuccessDeactivate() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.of(activeRoutine));

        // When
        assertDoesNotThrow(() -> routineService.toggleRoutineActive(memberId, routineId));

        // Then
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
    }

    @Test
    @DisplayName("루틴 활성화/비활성화 토글 성공 테스트 - 비활성화된 루틴을 활성화")
    void toggleRoutineActiveSuccessActivate() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.of(inactiveRoutine));

        // When
        assertDoesNotThrow(() -> routineService.toggleRoutineActive(memberId, routineId));

        // Then
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
    }

    @Test
    @DisplayName("루틴 활성화/비활성화 토글 실패 테스트 - 존재하지 않는 회원")
    void toggleRoutineActiveFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.toggleRoutineActive(memberId, routineId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository, never()).findByIdAndMember(anyLong(), any(Member.class));
    }

    @Test
    @DisplayName("루틴 활성화/비활성화 토글 실패 테스트 - 존재하지 않는 루틴")
    void toggleRoutineActiveFailRoutineNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.toggleRoutineActive(memberId, routineId));

        assertEquals("루틴을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
    }

    @Test
    @DisplayName("회원별 루틴 목록 조회 성공 테스트")
    void getRoutinesSuccess() {
        // Given
        List<Routine> routines = Arrays.asList(activeRoutine, inactiveRoutine);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findAllRoutinesByMember(member)).thenReturn(routines);

        // When
        List<RoutineResponse> responses = routineService.getRoutines(memberId);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());

        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findAllRoutinesByMember(member);
    }

    @Test
    @DisplayName("회원별 루틴 목록 조회 실패 테스트 - 존재하지 않는 회원")
    void getRoutinesFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.getRoutines(memberId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository, never()).findAllRoutinesByMember(any(Member.class));
    }

    @Test
    @DisplayName("빈 루틴 목록 조회 테스트")
    void getRoutinesEmptyList() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findAllRoutinesByMember(member)).thenReturn(Collections.emptyList());

        // When
        List<RoutineResponse> responses = routineService.getRoutines(memberId);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findAllRoutinesByMember(member);
    }

    @Test
    @DisplayName("날짜 범위 루틴 미리보기 조회 성공 테스트")
    void getRoutineForDateRangeSuccess() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(7);
        List<Routine> activeRoutines = List.of(activeRoutine);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findActiveRoutinesByMember(member)).thenReturn(activeRoutines);

        // When
        List<RoutinePreviewResponse> responses = routineService.getRoutineForDateRange(memberId, startDate, endDate);

        // Then
        assertNotNull(responses);

        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findActiveRoutinesByMember(member);
    }

    @Test
    @DisplayName("날짜 범위 루틴 미리보기 조회 실패 테스트 - 존재하지 않는 회원")
    void getRoutineForDateRangeFailMemberNotFound() {
        // Given
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = LocalDate.now().plusDays(7);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.getRoutineForDateRange(memberId, startDate, endDate));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository, never()).findActiveRoutinesByMember(any(Member.class));
    }

    @Test
    @DisplayName("루틴을 Todo로 변환 성공 테스트")
    void convertRoutineToTodoSuccess() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(1);
        LocalDateTime targetDateTime = targetDate.atStartOfDay();

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.of(activeRoutine));
        when(todoRepository.existsByMemberAndRoutineAndDueDate(any(Member.class), any(Routine.class), any(LocalDateTime.class)))
                .thenReturn(false);
        when(todoRepository.save(any(Todo.class))).thenReturn(mock(Todo.class));

        // When
        assertDoesNotThrow(() -> routineService.convertRoutineToTodo(memberId, routineId, targetDate));

        // Then
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
        verify(todoRepository).existsByMemberAndRoutineAndDueDate(any(Member.class), any(Routine.class), any(LocalDateTime.class));
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    @DisplayName("루틴을 Todo로 변환 실패 테스트 - 존재하지 않는 회원")
    void convertRoutineToTodoFailMemberNotFound() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(1);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.convertRoutineToTodo(memberId, routineId, targetDate));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository, never()).findByIdAndMember(anyLong(), any(Member.class));
    }

    @Test
    @DisplayName("루틴을 Todo로 변환 실패 테스트 - 존재하지 않는 루틴")
    void convertRoutineToTodoFailRoutineNotFound() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(1);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> routineService.convertRoutineToTodo(memberId, routineId, targetDate));

        assertEquals("루틴을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
    }

    @Test
    @DisplayName("루틴을 Todo로 변환 실패 테스트 - 비활성화된 루틴")
    void convertRoutineToTodoFailInactiveRoutine() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(1);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.of(inactiveRoutine));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> routineService.convertRoutineToTodo(memberId, routineId, targetDate));

        assertEquals("비활성화된 루틴입니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
        verify(todoRepository, never()).existsByMemberAndRoutineAndDueDate(any(Member.class), any(Routine.class), any(LocalDateTime.class));
    }

    @Test
    @DisplayName("루틴을 Todo로 변환 실패 테스트 - 이미 생성된 Todo 있음")
    void convertRoutineToTodoFailTodoExists() {
        // Given
        LocalDate targetDate = LocalDate.now().plusDays(1);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(routineRepository.findByIdAndMember(routineId, member)).thenReturn(Optional.of(activeRoutine));
        when(todoRepository.existsByMemberAndRoutineAndDueDate(any(Member.class), any(Routine.class), any(LocalDateTime.class)))
                .thenReturn(true);

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> routineService.convertRoutineToTodo(memberId, routineId, targetDate));

        assertEquals("이미 해당 날짜에 생성된 Todo가 있습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(routineRepository).findByIdAndMember(routineId, member);
        verify(todoRepository).existsByMemberAndRoutineAndDueDate(any(Member.class), any(Routine.class), any(LocalDateTime.class));
        verify(todoRepository, never()).save(any(Todo.class));
    }
}