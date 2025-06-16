package com.memory.dto.calendar;

import com.memory.domain.calendar.AnniversaryEvent;
import com.memory.domain.calendar.CalendarEventType;
import com.memory.domain.calendar.PersonalEvent;
import com.memory.domain.calendar.RelationshipEvent;
import com.memory.domain.calendar.repeat.RepeatSetting;
import com.memory.domain.calendar.repeat.RepeatType;
import com.memory.domain.member.Member;
import com.memory.domain.relationship.Relationship;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class CalendarEventRequest {

    @Getter
    public static class Create extends BaseCalendarEventRequest.Create {
        @NotNull(message = "이벤트 타입은 필수 입력값입니다.")
        private CalendarEventType eventType;

        // Personal event fields
        private final RepeatType repeatType;
        private final Integer repeatInterval;
        private final LocalDate repeatEndDate;

        public Create(String title, String description, LocalDateTime startDateTime,
                     LocalDateTime endDateTime, String location, CalendarEventType eventType,
                     RepeatType repeatType, Integer repeatInterval, LocalDate repeatEndDate) {
            super(title, description, startDateTime, endDateTime, location);
            this.eventType = eventType;
            this.repeatType = repeatType;
            this.repeatInterval = repeatInterval;
            this.repeatEndDate = repeatEndDate;
        }

        public PersonalEvent toPersonalEvent(Member member) {
            RepeatSetting repeatSetting = RepeatSetting.of(
                    repeatType != null ? repeatType : RepeatType.NONE,
                    repeatInterval,
                    repeatEndDate
            );
            return PersonalEvent.create(
                    title, description, startDateTime, endDateTime, location, member, repeatSetting
            );
        }

        public RelationshipEvent toRelationshipEvent(Member member, Relationship relationship) {
            return RelationshipEvent.create(
                    title, description, startDateTime, endDateTime, location, member, relationship
            );
        }

        public AnniversaryEvent toAnniversaryEvent(Member member, Relationship relationship) {
            return AnniversaryEvent.create(
                    title, description, startDateTime, endDateTime, location, member, relationship
            );
        }
    }
}