package com.memory.dto.calendar

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDateTime


abstract class BaseCalendarEventRequest {

    abstract class Create protected constructor(
        title: String,
        description: String?,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
        location: String?
    ) {
        @field:NotBlank(message = "제목은 필수 입력값입니다.")
        open var title: String = title
            protected set

        open var description: String? = description
            protected set

        @field:NotNull(message = "시작 일시는 필수 입력값입니다.")
        open var startDateTime: LocalDateTime = startDateTime
            protected set

        open var endDateTime: LocalDateTime? = endDateTime
            protected set

        open var location: String? = location
            protected set
    }

    abstract class Update protected constructor(
        title: String,
        description: String?,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
        location: String?
    ) {
        @field:NotBlank(message = "제목은 필수 입력값입니다.")
        open var title: String = title
            protected set

        open var description: String? = description
            protected set

        @field:NotNull(message = "시작 일시는 필수 입력값입니다.")
        open var startDateTime: LocalDateTime = startDateTime
            protected set

        open var endDateTime: LocalDateTime? = endDateTime
            protected set

        open var location: String? = location
            protected set
    }
}