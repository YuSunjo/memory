package com.memory.domain.todo.repository;

import com.memory.domain.member.Member;
import com.memory.domain.todo.Todo;

import java.time.LocalDateTime;
import java.util.List;

public interface TodoRepositoryCustom {

    List<Todo> findByMemberAndDueDateBetween(Member member, LocalDateTime startDateTime, LocalDateTime endDateTime);

}