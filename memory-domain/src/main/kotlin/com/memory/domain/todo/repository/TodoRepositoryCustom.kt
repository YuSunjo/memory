package com.memory.domain.todo.repository

import com.memory.domain.member.Member
import com.memory.domain.routine.Routine
import com.memory.domain.todo.Todo
import java.time.LocalDateTime

interface TodoRepositoryCustom {

    fun findByMemberAndDueDateBetween(member: Member?, startDateTime: LocalDateTime?, endDateTime: LocalDateTime?): List<Todo>

    fun existsByMemberAndRoutineAndDueDate(member: Member?, routine: Routine?, dueDate: LocalDateTime?): Boolean
}
