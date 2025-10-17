package com.memory.service.todo;

import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.todo.Todo;
import com.memory.domain.todo.repository.TodoRepository;
import com.memory.dto.todo.TodoRequest;
import com.memory.dto.todo.response.TodoResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {

    private final MemberRepository memberRepository;
    private final TodoRepository todoRepository;

    @Transactional
    public TodoResponse createTodo(Long memberId, TodoRequest.Create request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Todo todo = request.toEntity(member);

        Todo savedTodo = todoRepository.save(todo);
        return TodoResponse.from(savedTodo);
    }

    @Transactional
    public TodoResponse updateTodo(Long memberId, Long todoId, TodoRequest.Update request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundException("할 일을 찾을 수 없습니다."));

        if (!todo.isOwner(member)) {
            throw new ValidationException("해당 할 일에 대한 권한이 없습니다.");
        }

        todo.update(
                request.getTitle(),
                request.getContent(),
                request.getDueDate()
        );

        return TodoResponse.from(todo);
    }

    @Transactional
    public TodoResponse updateTodoStatus(Long memberId, Long todoId, TodoRequest.UpdateStatus request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundException("할 일을 찾을 수 없습니다."));

        if (!todo.isOwner(member)) {
            throw new ValidationException("해당 할 일에 대한 권한이 없습니다.");
        }

        if (request.getCompleted()) {
            todo.complete();
        } else {
            todo.incomplete();
        }

        return TodoResponse.from(todo);
    }

    @Transactional
    public void deleteTodo(Long memberId, Long todoId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new NotFoundException("할 일을 찾을 수 없습니다."));

        if (!todo.isOwner(member)) {
            throw new ValidationException("해당 할 일에 대한 권한이 없습니다.");
        }

        todo.updateDelete();
    }

    @Transactional(readOnly = true)
    public List<TodoResponse> getTodosByDateRange(Long memberId, LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Todo> todos = todoRepository.findByMemberAndDueDateBetween(member, startDateTime, endDateTime);
        return todos.stream()
                .map(TodoResponse::from)
                .collect(Collectors.toList());
    }
}
