package com.memory.dto.calendar;

import com.memory.domain.calendar.AnniversaryEvent;
import com.memory.domain.calendar.CalendarEventType;
import com.memory.domain.calendar.PersonalEvent;
import com.memory.domain.calendar.RelationshipEvent;
import com.memory.domain.member.Member;
import com.memory.domain.relationship.Relationship;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

public class CalendarEventRequest {

    @Getter
    public static class Create extends BaseCalendarEventRequest.Create {
        @NotNull(message = "이벤트 타입은 필수 입력값입니다.")
        private CalendarEventType eventType;

        private final Boolean isDday;

        public Create(String title, String description, LocalDateTime startDateTime,
                     LocalDateTime endDateTime, String location, CalendarEventType eventType,
                     Boolean isDday) {
            super(title, description, startDateTime, endDateTime, location);
            this.eventType = eventType;
            this.isDday = isDday;
        }

        public PersonalEvent toPersonalEvent(Member member) {
            return PersonalEvent.create(
                    title, description, startDateTime, endDateTime, location, member
            );
        }

        public RelationshipEvent toRelationshipEvent(Member member, Relationship relationship) {
            return RelationshipEvent.create(
                    title, description, startDateTime, endDateTime, location, member, relationship
            );
        }

        public AnniversaryEvent toAnniversaryEvent(Member member, Relationship relationship) {
            return AnniversaryEvent.create(
                    title, description, startDateTime, endDateTime, location, member, relationship, isDday
            );
        }
    }

    @Getter
    public static class Update extends BaseCalendarEventRequest.Update {
        @NotNull(message = "이벤트 타입은 필수 입력값입니다.")
        private CalendarEventType eventType;

        private final Boolean isDday;

        public Update(String title, String description, LocalDateTime startDateTime,
                     LocalDateTime endDateTime, String location, CalendarEventType eventType,
                     Boolean isDday) {
            super(title, description, startDateTime, endDateTime, location);
            this.eventType = eventType;
            this.isDday = isDday;
        }

    }
}
