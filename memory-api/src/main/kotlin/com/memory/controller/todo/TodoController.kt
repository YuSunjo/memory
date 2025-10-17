package com.memory.controller.todo

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.todo.TodoRequest
import com.memory.dto.todo.TodoRequest.ConvertRoutine
import com.memory.dto.todo.TodoRequest.UpdateStatus
import com.memory.dto.todo.response.CombinedTodoResponse
import com.memory.dto.todo.response.TodoResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.todo.TodoService
import com.memory.useCase.todo.TodoUseCase
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@RestController
@Tag(name = "Todo", description = "Todo API")
class TodoController(
    private val todoUseCase: TodoUseCase,
    private val todoService: TodoService,
) {
    @SecuredApi(summary = "할 일 생성", description = "새로운 할 일을 생성합니다.", response = TodoResponse::class)
    @Auth
    @PostMapping("api/v1/todos")
    fun createTodo(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody request: @Valid TodoRequest.Create
    ): ServerResponse<TodoResponse?> {
        return success(todoService.createTodo(memberId, request))
    }

    @SecuredApi(summary = "할 일 수정", description = "기존 할 일을 수정합니다.", response = TodoResponse::class)
    @Auth
    @PutMapping("api/v1/todos/{todoId}")
    fun updateTodo(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable todoId: Long,
        @RequestBody request: @Valid TodoRequest.Update
    ): ServerResponse<TodoResponse?> {
        return success(todoService.updateTodo(memberId, todoId, request))
    }

    @SecuredApi(summary = "할 일 상태 변경", description = "할 일의 완료 상태를 변경합니다.", response = TodoResponse::class)
    @Auth
    @PatchMapping("api/v1/todos/{todoId}/status")
    fun updateTodoStatus(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable todoId: Long,
        @RequestBody request: @Valid UpdateStatus
    ): ServerResponse<TodoResponse?> {
        return success(todoService.updateTodoStatus(memberId, todoId, request))
    }

    @SecuredApi(summary = "할 일 삭제", description = "할 일을 삭제합니다.")
    @Auth
    @DeleteMapping("api/v1/todos/{todoId}")
    fun deleteTodo(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @PathVariable todoId: Long
    ): ServerResponse<String> {
        todoService.deleteTodo(memberId, todoId)
        return ServerResponse.OK
    }

    @SecuredApi(summary = "기간별 할 일 조회", description = "특정 기간 내의 할 일을 조회합니다.", response = TodoResponse::class)
    @Auth
    @GetMapping("api/v1/todos/date-range")
    fun getTodosByDateRange(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ServerResponse<MutableList<TodoResponse?>?> {
        return success(todoService.getTodosByDateRange(memberId, startDate, endDate))
    }

    @SecuredApi(
        summary = "Todo와 루틴 미리보기 조회",
        description = "실제 Todo와 루틴 미리보기를 함께 조회합니다.",
        response = CombinedTodoResponse::class
    )
    @Auth
    @GetMapping("api/v1/todos/combined")
    fun getCombinedTodos(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ServerResponse<CombinedTodoResponse?> {
        return success(todoUseCase.getCombinedTodosByDateRange(memberId, startDate, endDate))
    }

    @SecuredApi(summary = "루틴을 Todo로 변환", description = "루틴을 실제 Todo로 변환합니다.", response = TodoResponse::class)
    @Auth
    @PostMapping("api/v1/todos/convert-routine")
    fun convertRoutineToTodo(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody request: ConvertRoutine
    ): ServerResponse<TodoResponse?> {
        return success(
            todoUseCase.convertRoutineToTodo(
                memberId,
                request.routineId,
                request.targetDate
            )
        )
    }
}
