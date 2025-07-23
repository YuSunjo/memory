package com.memory.dto.todo.response;

import com.memory.domain.todo.Todo;
import com.memory.dto.member.response.MemberResponse;
import com.memory.dto.routine.response.RoutinePreviewResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TodoResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime dueDate;
    private final boolean completed;
    private final boolean isRoutine;
    private final Long routineId;
    private final MemberResponse member;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;

    private TodoResponse(Long id, String title, String content, LocalDateTime dueDate, 
                        boolean completed, boolean isRoutine, Long routineId,
                        MemberResponse member, LocalDateTime createDate, LocalDateTime updateDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.completed = completed;
        this.isRoutine = isRoutine;
        this.routineId = routineId;
        this.member = member;
        this.createDate = createDate;
        this.updateDate = updateDate;
    }

    public static TodoResponse from(Todo todo) {
        if (todo == null) {
            return null;
        }

        return new TodoResponse(
            todo.getId(),
            todo.getTitle(),
            todo.getContent(),
            todo.getDueDate(),
            todo.isCompleted(),
            todo.isRoutine(),
            todo.getRoutine() != null ? todo.getRoutine().getId() : null,
            MemberResponse.from(todo.getMember()),
            todo.getCreateDate(),
            todo.getUpdateDate()
        );
    }

    public boolean isConvertRoutine(RoutinePreviewResponse routine) {
        if (routine == null || !this.isRoutine) {
            return false;
        }

        return this.getDueDate().toLocalDate().isEqual(routine.getTargetDate()) &&
               this.getRoutineId() != null && this.getRoutineId().equals(routine.getRoutineId());
    }
}
