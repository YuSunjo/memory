package com.memory.domain.calendar;

import lombok.Getter;

@Getter
public enum CalendarEventType {
    PERSONAL("개인 일정"),
    ANNIVERSARY_EVENT("기념일"),
    RELATIONSHIP_EVENT("관계 일정");

    private final String description;

    CalendarEventType(String description) {
        this.description = description;
    }
}
