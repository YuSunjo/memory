package com.memory.dto.calendar.response;

import com.memory.domain.calendar.BaseCalendarEvent;
import com.memory.dto.member.response.MemberResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public abstract class BaseCalendarEventResponse {
    private final Long id;
    private final String title;
    private final String description;
    private final LocalDateTime startDateTime;
    private final LocalDateTime endDateTime;
    private final String location;
    private final MemberResponse member;
    private final LocalDateTime createDate;
    private final Integer dday;

    protected BaseCalendarEventResponse(Long id, String title, String description, 
                                      LocalDateTime startDateTime, LocalDateTime endDateTime, 
                                      String location, MemberResponse member, LocalDateTime createDate,
                                      Integer dday) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.member = member;
        this.createDate = createDate;
        this.dday = dday;
    }

    public static BaseCalendarEventResponse from(BaseCalendarEvent event) {
        if (event == null) {
            return null;
        }

        return switch (event.getClass().getSimpleName()) {
            case "PersonalEvent" -> PersonalEventResponse.from(event);
            case "AnniversaryEvent" -> AnniversaryEventResponse.from(event);
            case "RelationshipEvent" -> RelationshipEventResponse.from(event);
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getClass().getSimpleName());
        };
    }
}
