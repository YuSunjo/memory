package com.memory.service.todo

import com.memory.domain.member.repository.MemberRepository
import com.memory.domain.todo.Todo
import com.memory.domain.todo.repository.TodoRepository
import com.memory.dto.todo.TodoRequest
import com.memory.dto.todo.TodoRequest.UpdateStatus
import com.memory.dto.todo.response.TodoResponse
import com.memory.dto.todo.response.TodoResponse.Companion.from
import com.memory.exception.customException.NotFoundException
import com.memory.exception.customException.ValidationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import java.util.stream.Collectors

@Service
class TodoService(
    private val memberRepository: MemberRepository,
    private val todoRepository: TodoRepository,
) {
    @Transactional
    fun createTodo(memberId: Long?, request: TodoRequest.Create): TodoResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val todo = request.toEntity(member)

        val savedTodo = todoRepository.save<Todo>(todo)
        return from(savedTodo)
    }

    @Transactional
    fun updateTodo(memberId: Long?, todoId: Long, request: TodoRequest.Update): TodoResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val todo = todoRepository.findById(todoId)
            .orElseThrow { NotFoundException("할 일을 찾을 수 없습니다.") }

        if (!todo.isOwner(member)) {
            throw ValidationException("해당 할 일에 대한 권한이 없습니다.")
        }

        todo.update(
            request.title,
            request.content,
            request.dueDate
        )

        return from(todo)
    }

    @Transactional
    fun updateTodoStatus(memberId: Long?, todoId: Long, request: UpdateStatus): TodoResponse? {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val todo = todoRepository.findById(todoId)
            .orElseThrow { NotFoundException("할 일을 찾을 수 없습니다.") }

        if (!todo.isOwner(member)) {
            throw ValidationException("해당 할 일에 대한 권한이 없습니다.")
        }

        if (request.completed) {
            todo.complete()
        } else {
            todo.incomplete()
        }

        return from(todo)
    }

    @Transactional
    fun deleteTodo(memberId: Long?, todoId: Long) {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val todo = todoRepository.findById(todoId)
            .orElseThrow { NotFoundException("할 일을 찾을 수 없습니다.") }

        if (!todo.isOwner(member)) {
            throw ValidationException("해당 할 일에 대한 권한이 없습니다.")
        }

        todo.updateDelete()
    }

    @Transactional(readOnly = true)
    fun getTodosByDateRange(memberId: Long?, startDate: LocalDate, endDate: LocalDate): MutableList<TodoResponse?> {
        val startDateTime = startDate.atStartOfDay()
        val endDateTime = endDate.atTime(LocalTime.MAX)

        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val todos: List<Todo> =
            todoRepository.findByMemberAndDueDateBetween(member, startDateTime, endDateTime)
        return todos.stream()
            .map<TodoResponse?> { obj: Todo -> from(obj) }
            .collect(Collectors.toList())
    }
}
