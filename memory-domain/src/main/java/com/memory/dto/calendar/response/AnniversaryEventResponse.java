package com.memory.dto.calendar.response;

import com.memory.domain.calendar.AnniversaryEvent;
import com.memory.domain.calendar.BaseCalendarEvent;
import com.memory.dto.member.response.MemberResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AnniversaryEventResponse extends BaseCalendarEventResponse {
    private final MemberResponse relatedMember;

    public AnniversaryEventResponse(Long id, String title, String description, 
                             LocalDateTime startDateTime, LocalDateTime endDateTime, 
                             String location, MemberResponse member, LocalDateTime createDate,
                             MemberResponse relatedMember) {
        super(id, title, description, startDateTime, endDateTime, location, member, createDate);
        this.relatedMember = relatedMember;
    }

    public static AnniversaryEventResponse from(BaseCalendarEvent event) {
        if (!(event instanceof AnniversaryEvent anniversaryEvent)) {
            throw new IllegalArgumentException("Event is not an AnniversaryEvent");
        }

        return new AnniversaryEventResponse(
                anniversaryEvent.getId(),
                anniversaryEvent.getTitle(),
                anniversaryEvent.getDescription(),
                anniversaryEvent.getStartDateTime(),
                anniversaryEvent.getEndDateTime(),
                anniversaryEvent.getLocation(),
                MemberResponse.from(anniversaryEvent.getMember()),
                anniversaryEvent.getCreateDate(),
                MemberResponse.from(anniversaryEvent.getRelationship().getRelatedMember())
        );
    }
}