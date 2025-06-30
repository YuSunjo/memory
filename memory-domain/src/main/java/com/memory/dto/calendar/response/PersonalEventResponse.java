package com.memory.dto.calendar.response;

import com.memory.domain.calendar.BaseCalendarEvent;
import com.memory.domain.calendar.PersonalEvent;
import com.memory.dto.member.response.MemberResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
public class PersonalEventResponse extends BaseCalendarEventResponse {

    public PersonalEventResponse(Long id, String title, String description, 
                               LocalDateTime startDateTime, LocalDateTime endDateTime, 
                               String location, MemberResponse member, LocalDateTime createDate,
                               Integer dday) {
        super(id, title, description, startDateTime, endDateTime, location, member, createDate, dday);
    }

    public static PersonalEventResponse from(BaseCalendarEvent event) {
        if (!(event instanceof PersonalEvent personalEvent)) {
            throw new IllegalArgumentException("Event is not a PersonalEvent");
        }

        Integer dday = calculateDday(personalEvent.getStartDateTime());

        return new PersonalEventResponse(
                personalEvent.getId(),
                personalEvent.getTitle(),
                personalEvent.getDescription(),
                personalEvent.getStartDateTime(),
                personalEvent.getEndDateTime(),
                personalEvent.getLocation(),
                MemberResponse.from(personalEvent.getMember()),
                personalEvent.getCreateDate(),
                dday
        );
    }

    private static Integer calculateDday(LocalDateTime eventDateTime) {
        LocalDate today = LocalDate.now();
        LocalDate eventDate = eventDateTime.toLocalDate();

        return (int) ChronoUnit.DAYS.between(today, eventDate);
    }
}
