package com.memory.dto.todo;

import com.memory.domain.common.repeat.RepeatSetting;
import com.memory.domain.common.repeat.RepeatType;
import com.memory.domain.member.Member;
import com.memory.domain.todo.Todo;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TodoRequest {

    @Getter
    public static class Create {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        private final String content;

        @NotNull(message = "마감일시는 필수 입력값입니다.")
        private LocalDateTime dueDate;

        private final RepeatType repeatType;
        private final Integer repeatInterval;
        private final LocalDate repeatEndDate;

        public Create(String title, String content, LocalDateTime dueDate,
                     RepeatType repeatType, Integer repeatInterval, LocalDate repeatEndDate) {
            this.title = title;
            this.content = content;
            this.dueDate = dueDate;
            this.repeatType = repeatType;
            this.repeatInterval = repeatInterval;
            this.repeatEndDate = repeatEndDate;
        }

        public RepeatSetting toRepeatSetting() {
            return RepeatSetting.of(
                    repeatType != null ? repeatType : RepeatType.NONE,
                    repeatInterval,
                    repeatEndDate
            );
        }

        public Todo toEntity(Member member) {
            return Todo.create(title, content, dueDate, member, toRepeatSetting());
        }
    }

    @Getter
    public static class Update {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        private String title;

        private final String content;

        @NotNull(message = "마감일시는 필수 입력값입니다.")
        private LocalDateTime dueDate;

        private RepeatType repeatType;
        private Integer repeatInterval;
        private LocalDate repeatEndDate;

        public Update(String title, String content, LocalDateTime dueDate) {
            this.title = title;
            this.content = content;
            this.dueDate = dueDate;
        }

        public Update(String title, String content, LocalDateTime dueDate, 
                     RepeatType repeatType, Integer repeatInterval, LocalDate repeatEndDate) {
            this.title = title;
            this.content = content;
            this.dueDate = dueDate;
            this.repeatType = repeatType;
            this.repeatInterval = repeatInterval;
            this.repeatEndDate = repeatEndDate;
        }

        public RepeatSetting toRepeatSetting() {
            return RepeatSetting.of(
                    repeatType != null ? repeatType : RepeatType.NONE,
                    repeatInterval,
                    repeatEndDate
            );
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
}
