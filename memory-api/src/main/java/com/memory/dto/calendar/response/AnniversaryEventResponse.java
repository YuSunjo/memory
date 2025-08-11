package com.memory.dto.calendar.response;

import com.memory.domain.calendar.AnniversaryEvent;
import com.memory.domain.calendar.BaseCalendarEvent;
import com.memory.dto.member.response.MemberResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.LocalDate;

@Getter
public class AnniversaryEventResponse extends BaseCalendarEventResponse {
    private final MemberResponse relatedMember;
    private final Boolean isDday;

    public AnniversaryEventResponse(Long id, String title, String description, 
                             LocalDateTime startDateTime, LocalDateTime endDateTime, 
                             String location, MemberResponse member, LocalDateTime createDate,
                             MemberResponse relatedMember, Boolean isDday, Integer dday) {
        super(id, title, description, startDateTime, endDateTime, location, member, createDate, dday);
        this.relatedMember = relatedMember;
        this.isDday = isDday;
    }

    public static AnniversaryEventResponse from(BaseCalendarEvent event) {
        if (!(event instanceof AnniversaryEvent anniversaryEvent)) {
            throw new IllegalArgumentException("Event is not an AnniversaryEvent");
        }

        Integer dday = calculateDday(anniversaryEvent.getStartDateTime());

        return new AnniversaryEventResponse(
                anniversaryEvent.getId(),
                anniversaryEvent.getTitle(),
                anniversaryEvent.getDescription(),
                anniversaryEvent.getStartDateTime(),
                anniversaryEvent.getEndDateTime(),
                anniversaryEvent.getLocation(),
                MemberResponse.from(anniversaryEvent.getMember()),
                anniversaryEvent.getCreateDate(),
                MemberResponse.from(anniversaryEvent.getRelationship().getRelatedMember()),
                anniversaryEvent.getIsDday(),
                dday
        );
    }

    private static Integer calculateDday(LocalDateTime eventDateTime) {
        LocalDate today = LocalDate.now();
        LocalDate eventDate = eventDateTime.toLocalDate();
        return (int) ChronoUnit.DAYS.between(today, eventDate);
    }
}
