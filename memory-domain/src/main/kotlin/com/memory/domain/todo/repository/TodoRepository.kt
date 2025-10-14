package com.memory.domain.todo.repository

import com.memory.domain.todo.Todo
import org.springframework.data.jpa.repository.JpaRepository

interface TodoRepository : JpaRepository<Todo, Long>, TodoRepositoryCustom
