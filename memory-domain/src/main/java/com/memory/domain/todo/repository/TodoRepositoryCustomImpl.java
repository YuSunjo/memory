package com.memory.domain.todo.repository;

import com.memory.domain.member.Member;
import com.memory.domain.todo.Todo;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.memory.domain.todo.QTodo.todo;

@RequiredArgsConstructor
public class TodoRepositoryCustomImpl implements TodoRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Todo> findByMemberAndDueDateBetween(Member member, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return queryFactory
                .selectFrom(todo)
                .where(
                        todo.member.eq(member)
                        .and(todo.dueDate.between(startDateTime, endDateTime))
                        .and(todo.deleteDate.isNull())
                )
                .fetch();
    }
}
