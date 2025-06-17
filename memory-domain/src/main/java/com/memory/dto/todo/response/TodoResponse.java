package com.memory.dto.todo.response;

import com.memory.domain.common.repeat.RepeatType;
import com.memory.domain.todo.Todo;
import com.memory.dto.member.response.MemberResponse;
import lombok.Getter;

import java.time.LocalDate;

import java.time.LocalDateTime;

@Getter
public class TodoResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final LocalDateTime dueDate;
    private final boolean completed;
    private final MemberResponse member;
    private final LocalDateTime createDate;
    private final LocalDateTime updateDate;
    private final RepeatType repeatType;
    private final Integer repeatInterval;
    private final LocalDate repeatEndDate;

    private TodoResponse(Long id, String title, String content, LocalDateTime dueDate, 
                        boolean completed, MemberResponse member, 
                        LocalDateTime createDate, LocalDateTime updateDate,
                        RepeatType repeatType, Integer repeatInterval, LocalDate repeatEndDate) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.completed = completed;
        this.member = member;
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.repeatType = repeatType;
        this.repeatInterval = repeatInterval;
        this.repeatEndDate = repeatEndDate;
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
            MemberResponse.from(todo.getMember()),
            todo.getCreateDate(),
            todo.getUpdateDate(),
            todo.getRepeatSetting().getRepeatType(),
            todo.getRepeatSetting().getInterval(),
            todo.getRepeatSetting().getEndDate()
        );
    }
}
