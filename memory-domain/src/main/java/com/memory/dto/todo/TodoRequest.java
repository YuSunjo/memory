package com.memory.dto.todo;

import com.memory.domain.member.Member;
import com.memory.domain.todo.Todo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

public class TodoRequest {

    @Getter
    public static class Create {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        private final String content;

        @NotNull(message = "마감일시는 필수 입력값입니다.")
        private LocalDateTime dueDate;

        public Create(String title, String content, LocalDateTime dueDate) {
            this.title = title;
            this.content = content;
            this.dueDate = dueDate;
        }

        public Todo toEntity(Member member) {
            return Todo.create(title, content, dueDate, member);
        }
    }

    @Getter
    public static class Update {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        private final String content;

        @NotNull(message = "마감일시는 필수 입력값입니다.")
        private LocalDateTime dueDate;

        public Update(String title, String content, LocalDateTime dueDate) {
            this.title = title;
            this.content = content;
            this.dueDate = dueDate;
        }
    }

    @Getter
    public static class UpdateStatus {
        @NotNull(message = "완료 상태는 필수 입력값입니다.")
        private Boolean completed;

        public UpdateStatus(Boolean completed) {
            this.completed = completed;
        }
    }

    @Getter
    public static class ConvertRoutine {
        @NotNull(message = "루틴 ID는 필수 입력값입니다.")
        private Long routineId;

        @NotNull(message = "대상 날짜는 필수 입력값입니다.")
        private java.time.LocalDate targetDate;

        public ConvertRoutine(Long routineId, java.time.LocalDate targetDate) {
            this.routineId = routineId;
            this.targetDate = targetDate;
        }
    }
}
