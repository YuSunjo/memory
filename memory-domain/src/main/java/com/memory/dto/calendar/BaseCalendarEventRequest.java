package com.memory.dto.calendar;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

public abstract class BaseCalendarEventRequest {
    
    @Getter
    public static abstract class Create {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        protected String title;

        protected String description;

        @NotNull(message = "시작 일시는 필수 입력값입니다.")
        protected LocalDateTime startDateTime;

        protected LocalDateTime endDateTime;

        protected String location;

        protected Create(String title, String description, LocalDateTime startDateTime, 
                       LocalDateTime endDateTime, String location) {
            this.title = title;
            this.description = description;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.location = location;
        }
    }

    @Getter
    public static abstract class Update {
        @NotBlank(message = "제목은 필수 입력값입니다.")
        protected String title;

        protected String description;

        @NotNull(message = "시작 일시는 필수 입력값입니다.")
        protected LocalDateTime startDateTime;

        protected LocalDateTime endDateTime;

        protected String location;

        protected Update(String title, String description, LocalDateTime startDateTime, 
                       LocalDateTime endDateTime, String location) {
            this.title = title;
            this.description = description;
            this.startDateTime = startDateTime;
            this.endDateTime = endDateTime;
            this.location = location;
        }
    }

}