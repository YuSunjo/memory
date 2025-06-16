package com.memory.dto.calendar.response;

import com.memory.domain.calendar.BaseCalendarEvent;
import com.memory.domain.calendar.RelationshipEvent;
import com.memory.dto.member.response.MemberResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RelationshipEventResponse extends BaseCalendarEventResponse {
    private final MemberResponse relatedMember;

    public RelationshipEventResponse(Long id, String title, String description, 
                            LocalDateTime startDateTime, LocalDateTime endDateTime, 
                            String location, MemberResponse member, LocalDateTime createDate,
                            MemberResponse relatedMember) {
        super(id, title, description, startDateTime, endDateTime, location, member, createDate);
        this.relatedMember = relatedMember;
    }

    public static RelationshipEventResponse from(BaseCalendarEvent event) {
        if (!(event instanceof RelationshipEvent relationshipEvent)) {
            throw new IllegalArgumentException("Event is not a RelationshipEvent");
        }

        return new RelationshipEventResponse(
                relationshipEvent.getId(),
                relationshipEvent.getTitle(),
                relationshipEvent.getDescription(),
                relationshipEvent.getStartDateTime(),
                relationshipEvent.getEndDateTime(),
                relationshipEvent.getLocation(),
                MemberResponse.from(relationshipEvent.getMember()),
                relationshipEvent.getCreateDate(),
                MemberResponse.from(relationshipEvent.getRelationship().getRelatedMember())
        );
    }
}