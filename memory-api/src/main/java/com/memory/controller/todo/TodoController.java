package com.memory.controller.todo;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.todo.TodoRequest;
import com.memory.dto.todo.response.CombinedTodoResponse;
import com.memory.dto.todo.response.TodoResponse;
import com.memory.response.ServerResponse;
import com.memory.service.todo.TodoService;
import com.memory.useCase.todo.TodoUseCase;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Todo", description = "Todo API")
public class TodoController {

    private final TodoUseCase todoUseCase;
    private final TodoService todoService;

    @ApiOperations.SecuredApi(
        summary = "할 일 생성",
        description = "새로운 할 일을 생성합니다.",
        response = TodoResponse.class
    )
    @Auth
    @PostMapping("api/v1/todos")
    public ServerResponse<TodoResponse> createTodo(
            @Parameter(hidden = true) @MemberId Long memberId, 
            @RequestBody @Valid TodoRequest.Create request) {
        return ServerResponse.success(todoService.createTodo(memberId, request));
    }

    @ApiOperations.SecuredApi(
        summary = "할 일 수정",
        description = "기존 할 일을 수정합니다.",
        response = TodoResponse.class
    )
    @Auth
    @PutMapping("api/v1/todos/{todoId}")
    public ServerResponse<TodoResponse> updateTodo(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long todoId,
            @RequestBody @Valid TodoRequest.Update request) {
        return ServerResponse.success(todoService.updateTodo(memberId, todoId, request));
    }

    @ApiOperations.SecuredApi(
        summary = "할 일 상태 변경",
        description = "할 일의 완료 상태를 변경합니다.",
        response = TodoResponse.class
    )
    @Auth
    @PatchMapping("api/v1/todos/{todoId}/status")
    public ServerResponse<TodoResponse> updateTodoStatus(
            @Parameter(hidden = true) @MemberId Long memberId,
            @PathVariable Long todoId,
            @RequestBody @Valid TodoRequest.UpdateStatus request) {
        return ServerResponse.success(todoService.updateTodoStatus(memberId, todoId, request));
    }

    @ApiOperations.SecuredApi(
        summary = "할 일 삭제",
        description = "할 일을 삭제합니다."
    )
    @Auth
    @DeleteMapping("api/v1/todos/{todoId}")
    public ServerResponse<String> deleteTodo(
            @Parameter(hidden = true) @MemberId Long memberId, 
            @PathVariable Long todoId) {
        todoService.deleteTodo(memberId, todoId);
        return ServerResponse.OK;
    }

    @ApiOperations.SecuredApi(
        summary = "기간별 할 일 조회",
        description = "특정 기간 내의 할 일을 조회합니다.",
        response = TodoResponse.class
    )
    @Auth
    @GetMapping("api/v1/todos/date-range")
    public ServerResponse<List<TodoResponse>> getTodosByDateRange(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ServerResponse.success(todoService.getTodosByDateRange(memberId, startDate, endDate));
    }

    @ApiOperations.SecuredApi(
        summary = "Todo와 루틴 미리보기 조회",
        description = "실제 Todo와 루틴 미리보기를 함께 조회합니다.",
        response = CombinedTodoResponse.class
    )
    @Auth
    @GetMapping("api/v1/todos/combined")
    public ServerResponse<CombinedTodoResponse> getCombinedTodos(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ServerResponse.success(todoUseCase.getCombinedTodosByDateRange(memberId, startDate, endDate));
    }

    @ApiOperations.SecuredApi(
        summary = "루틴을 Todo로 변환",
        description = "루틴을 실제 Todo로 변환합니다.",
        response = TodoResponse.class
    )
    @Auth
    @PostMapping("api/v1/todos/convert-routine")
    public ServerResponse<TodoResponse> convertRoutineToTodo(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody TodoRequest.ConvertRoutine request) {
        return ServerResponse.success(todoUseCase.convertRoutineToTodo(memberId, request.getRoutineId(), request.getTargetDate()));
    }
}
