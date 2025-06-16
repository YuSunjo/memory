package com.memory.service.calendar;

import com.memory.domain.member.Member;
import com.memory.domain.relationship.Relationship;
import com.memory.exception.customException.ValidationException;

public class CalendarEventServiceUtils {

    public static void validateRelationshipMember(Member member, Relationship relationship) {
        if (!relationship.getMember().getId().equals(member.getId()) &&
            !relationship.getRelatedMember().getId().equals(member.getId())) {
            throw new ValidationException("해당 관계에 속한 회원이 아닙니다.");
        }
    }

}
