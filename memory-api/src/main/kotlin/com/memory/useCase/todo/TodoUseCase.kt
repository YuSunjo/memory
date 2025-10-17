package com.memory.useCase.todo

import com.memory.domain.member.repository.MemberRepository
import com.memory.dto.routine.response.RoutinePreviewResponse
import com.memory.dto.todo.response.CombinedTodoResponse
import com.memory.dto.todo.response.CombinedTodoResponse.Companion.of
import com.memory.dto.todo.response.TodoResponse
import com.memory.exception.customException.NotFoundException
import com.memory.service.routine.RoutineService
import com.memory.service.todo.TodoService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class TodoUseCase(
    private val todoService: TodoService,
    private val routineService: RoutineService,
    private val memberRepository: MemberRepository,
) {
    // 실제 Todo와 루틴 미리보기를 함께 조회하는 새로운 메서드
    @Transactional(readOnly = true)
    fun getCombinedTodosByDateRange(memberId: Long?, startDate: LocalDate, endDate: LocalDate): CombinedTodoResponse {
        val actualTodos = todoService.getTodosByDateRange(memberId, startDate, endDate)

        // 루틴 미리보기 조회
        val routinePreviews = routineService.getRoutineForDateRange(memberId, startDate, endDate)

        // 루틴에서 todo로 변환된 경우는 제외
        val routinesExcludingTodos = routinePreviews.stream()
            .filter { routine: RoutinePreviewResponse? ->
                actualTodos.stream()
                    .noneMatch { todo: TodoResponse? -> todo?.isConvertRoutine(routine) == true }
            }
            .toList()
        return of(actualTodos, routinesExcludingTodos)
    }

    @Transactional
    fun convertRoutineToTodo(memberId: Long?, routineId: Long?, targetDate: LocalDate): TodoResponse? {
        routineService.convertRoutineToTodo(memberId, routineId, targetDate)

        memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        // 해당 날짜의 Todo 목록을 조회해서 방금 생성된 것을 찾아 반환
        val todosForDate = todoService.getTodosByDateRange(memberId, targetDate, targetDate)
        return todosForDate.stream()
            .filter({
                todo: TodoResponse? -> todo!!.isRoutine && todo.routineId == routineId && todo.dueDate?.toLocalDate() == targetDate
            })
            .findFirst()
            .orElseThrow { NotFoundException("생성된 Todo를 찾을 수 없습니다.") }
    }
}
