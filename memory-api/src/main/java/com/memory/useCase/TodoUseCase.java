package com.memory.useCase;

import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.routine.response.RoutinePreviewResponse;
import com.memory.dto.todo.response.CombinedTodoResponse;
import com.memory.dto.todo.response.TodoResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.service.routine.RoutineService;
import com.memory.service.todo.TodoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TodoUseCase {

    private final TodoService todoService;
    private final RoutineService routineService;

    private final MemberRepository memberRepository;

    // 실제 Todo와 루틴 미리보기를 함께 조회하는 새로운 메서드
    @Transactional(readOnly = true)
    public CombinedTodoResponse getCombinedTodosByDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {
        List<TodoResponse> actualTodos  = todoService.getTodosByDateRange(memberId, startDate, endDate);

        // 루틴 미리보기 조회
        List<RoutinePreviewResponse> routinePreviews = routineService.getRoutinePreviewsForDateRange(memberId, startDate, endDate);

        return CombinedTodoResponse.builder()
                .actualTodos(actualTodos)
                .routinePreviews(routinePreviews)
                .build();
    }

    @Transactional
    public TodoResponse convertRoutineToTodo(Long memberId, Long routineId, LocalDate targetDate) {
        routineService.convertRoutineToTodo(memberId, routineId, targetDate);

        memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        // 해당 날짜의 Todo 목록을 조회해서 방금 생성된 것을 찾아 반환
        List<TodoResponse> todosForDate = todoService.getTodosByDateRange(memberId, targetDate, targetDate);
        return todosForDate.stream()
                .filter(TodoResponse::isRoutine)
                .findFirst()
                .orElseThrow(() -> new NotFoundException("생성된 Todo를 찾을 수 없습니다."));
    }

}
