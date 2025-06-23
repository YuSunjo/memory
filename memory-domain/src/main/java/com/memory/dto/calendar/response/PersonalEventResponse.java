package com.memory.dto.calendar.response;

import com.memory.domain.calendar.BaseCalendarEvent;
import com.memory.domain.calendar.PersonalEvent;
import com.memory.dto.member.response.MemberResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PersonalEventResponse extends BaseCalendarEventResponse {

    public PersonalEventResponse(Long id, String title, String description, 
                               LocalDateTime startDateTime, LocalDateTime endDateTime, 
                               String location, MemberResponse member, LocalDateTime createDate) {
        super(id, title, description, startDateTime, endDateTime, location, member, createDate);
    }

    public static PersonalEventResponse from(BaseCalendarEvent event) {
        if (!(event instanceof PersonalEvent personalEvent)) {
            throw new IllegalArgumentException("Event is not a PersonalEvent");
        }

        return new PersonalEventResponse(
                personalEvent.getId(),
                personalEvent.getTitle(),
                personalEvent.getDescription(),
                personalEvent.getStartDateTime(),
                personalEvent.getEndDateTime(),
                personalEvent.getLocation(),
                MemberResponse.from(personalEvent.getMember()),
                personalEvent.getCreateDate()
        );
    }
}
