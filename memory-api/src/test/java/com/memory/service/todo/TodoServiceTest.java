package com.memory.service.todo;

import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.todo.Todo;
import com.memory.domain.todo.repository.TodoRepository;
import com.memory.dto.todo.TodoRequest;
import com.memory.dto.todo.response.TodoResponse;
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
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TodoRepository todoRepository;

    @InjectMocks
    private TodoService todoService;

    private Member member;
    private Member otherMember;
    private Todo todo;
    private TodoRequest.Create createRequest;
    private TodoRequest.Update updateRequest;
    private TodoRequest.UpdateStatus updateStatusRequest;

    private final Long memberId = 1L;
    private final Long otherMemberId = 2L;
    private final Long todoId = 1L;
    private final String title = "테스트 할 일";
    private final String content = "테스트 내용";
    private final LocalDateTime dueDate = LocalDateTime.of(2025, 12, 31, 23, 59);

    @BeforeEach
    void setUp() {
        // Member 객체 생성
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        otherMember = new Member("다른 사용자", "otheruser", "other@example.com", "encodedPassword");
        setId(otherMember, otherMemberId);

        // Todo 객체 생성
        todo = Todo.create(title, content, dueDate, member);
        setId(todo, todoId);

        // Request 객체들 생성
        createRequest = createTodoCreateRequest();
        updateRequest = createTodoUpdateRequest();
        updateStatusRequest = createUpdateStatusRequest();
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

    private TodoRequest.Create createTodoCreateRequest() {
        return new TodoRequest.Create(title, content, dueDate);
    }

    private TodoRequest.Update createTodoUpdateRequest() {
        return new TodoRequest.Update("수정된 제목", "수정된 내용", dueDate.plusDays(1));
    }

    private TodoRequest.UpdateStatus createUpdateStatusRequest() {
        return new TodoRequest.UpdateStatus(true);
    }

    @Test
    @DisplayName("할 일 생성 성공 테스트")
    void createTodoSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.save(any(Todo.class))).thenReturn(todo);

        // When
        TodoResponse response = todoService.createTodo(memberId, createRequest);

        // Then
        assertNotNull(response);
        assertEquals(todoId, response.getId());
        assertEquals(title, response.getTitle());
        assertEquals(content, response.getContent());
        assertEquals(dueDate, response.getDueDate());

        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).save(any(Todo.class));
    }

    @Test
    @DisplayName("할 일 생성 실패 테스트 - 존재하지 않는 회원")
    void createTodoFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> todoService.createTodo(memberId, createRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository, never()).save(any(Todo.class));
    }

    @Test
    @DisplayName("할 일 수정 성공 테스트")
    void updateTodoSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        // When
        TodoResponse response = todoService.updateTodo(memberId, todoId, updateRequest);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findById(todoId);
    }

    @Test
    @DisplayName("할 일 수정 실패 테스트 - 존재하지 않는 회원")
    void updateTodoFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> todoService.updateTodo(memberId, todoId, updateRequest));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("할 일 수정 실패 테스트 - 존재하지 않는 할 일")
    void updateTodoFailTodoNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> todoService.updateTodo(memberId, todoId, updateRequest));

        assertEquals("할 일을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findById(todoId);
    }

    @Test
    @DisplayName("할 일 수정 실패 테스트 - 권한 없음")
    void updateTodoFailNoPermission() {
        // Given
        Todo otherTodo = spy(todo);
        when(otherTodo.isOwner(member)).thenReturn(false); // isOwner가 true면 권한 없음
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(otherTodo));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> todoService.updateTodo(memberId, todoId, updateRequest));

        assertEquals("해당 할 일에 대한 권한이 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findById(todoId);
    }

    @Test
    @DisplayName("할 일 완료 상태 변경 성공 테스트 - 완료로 변경")
    void updateTodoStatusSuccessComplete() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        // When
        TodoResponse response = todoService.updateTodoStatus(memberId, todoId, updateStatusRequest);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findById(todoId);
    }

    @Test
    @DisplayName("할 일 완료 상태 변경 성공 테스트 - 미완료로 변경")
    void updateTodoStatusSuccessIncomplete() {
        // Given
        TodoRequest.UpdateStatus incompleteRequest = new TodoRequest.UpdateStatus(false);
        
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        // When
        TodoResponse response = todoService.updateTodoStatus(memberId, todoId, incompleteRequest);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findById(todoId);
    }

    @Test
    @DisplayName("할 일 완료 상태 변경 실패 테스트 - 권한 없음")
    void updateTodoStatusFailNoPermission() {
        // Given
        Todo otherTodo = spy(todo);
        when(otherTodo.isOwner(member)).thenReturn(false);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(otherTodo));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> todoService.updateTodoStatus(memberId, todoId, updateStatusRequest));

        assertEquals("해당 할 일에 대한 권한이 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findById(todoId);
    }

    @Test
    @DisplayName("할 일 삭제 성공 테스트")
    void deleteTodoSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(todo));

        // When
        assertDoesNotThrow(() -> todoService.deleteTodo(memberId, todoId));

        // Then
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findById(todoId);
    }

    @Test
    @DisplayName("할 일 삭제 실패 테스트 - 존재하지 않는 회원")
    void deleteTodoFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> todoService.deleteTodo(memberId, todoId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository, never()).findById(anyLong());
    }

    @Test
    @DisplayName("할 일 삭제 실패 테스트 - 존재하지 않는 할 일")
    void deleteTodoFailTodoNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> todoService.deleteTodo(memberId, todoId));

        assertEquals("할 일을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findById(todoId);
    }

    @Test
    @DisplayName("할 일 삭제 실패 테스트 - 권한 없음")
    void deleteTodoFailNoPermission() {
        // Given
        Todo otherTodo = spy(todo);
        when(otherTodo.isOwner(member)).thenReturn(false);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findById(todoId)).thenReturn(Optional.of(otherTodo));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> todoService.deleteTodo(memberId, todoId));

        assertEquals("해당 할 일에 대한 권한이 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findById(todoId);
    }

    @Test
    @DisplayName("날짜 범위로 할 일 목록 조회 성공 테스트")
    void getTodosByDateRangeSuccess() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Todo todo2 = Todo.create("두 번째 할 일", "두 번째 내용", dueDate.plusDays(1), 
                member);
        setId(todo2, 2L);

        List<Todo> todos = Arrays.asList(todo, todo2);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findByMemberAndDueDateBetween(member, startDateTime, endDateTime))
                .thenReturn(todos);

        // When
        List<TodoResponse> responses = todoService.getTodosByDateRange(memberId, startDate, endDate);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals(todoId, responses.get(0).getId());
        assertEquals(2L, responses.get(1).getId());

        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findByMemberAndDueDateBetween(member, startDateTime, endDateTime);
    }

    @Test
    @DisplayName("날짜 범위로 할 일 목록 조회 실패 테스트 - 존재하지 않는 회원")
    void getTodosByDateRangeFailMemberNotFound() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> todoService.getTodosByDateRange(memberId, startDate, endDate));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository, never()).findByMemberAndDueDateBetween(any(), any(), any());
    }

    @Test
    @DisplayName("날짜 범위로 할 일 목록 조회 테스트 - 빈 결과")
    void getTodosByDateRangeEmptyResult() {
        // Given
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(todoRepository.findByMemberAndDueDateBetween(member, startDateTime, endDateTime))
                .thenReturn(List.of());

        // When
        List<TodoResponse> responses = todoService.getTodosByDateRange(memberId, startDate, endDate);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());

        verify(memberRepository).findMemberById(memberId);
        verify(todoRepository).findByMemberAndDueDateBetween(member, startDateTime, endDateTime);
    }
}
