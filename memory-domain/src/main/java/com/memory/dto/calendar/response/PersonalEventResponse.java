package com.memory.dto.calendar.response;

import com.memory.domain.calendar.BaseCalendarEvent;
import com.memory.domain.calendar.PersonalEvent;
import com.memory.domain.common.repeat.RepeatType;
import com.memory.dto.member.response.MemberResponse;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class PersonalEventResponse extends BaseCalendarEventResponse {
    private final RepeatType repeatType;
    private final Integer repeatInterval;
    private final LocalDate repeatEndDate;

    public PersonalEventResponse(Long id, String title, String description, 
                               LocalDateTime startDateTime, LocalDateTime endDateTime, 
                               String location, MemberResponse member, LocalDateTime createDate,
                               RepeatType repeatType, Integer repeatInterval, LocalDate repeatEndDate) {
        super(id, title, description, startDateTime, endDateTime, location, member, createDate);
        this.repeatType = repeatType;
        this.repeatInterval = repeatInterval;
        this.repeatEndDate = repeatEndDate;
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
                personalEvent.getCreateDate(),
                personalEvent.getRepeatSetting().getRepeatType(),
                personalEvent.getRepeatSetting().getInterval(),
                personalEvent.getRepeatSetting().getEndDate()
        );
    }
}
