package com.memory.service.calendar

import com.memory.domain.member.Member
import com.memory.domain.relationship.Relationship
import com.memory.exception.customException.ValidationException

object CalendarEventServiceUtils {
    fun validateRelationshipMember(member: Member?, relationship: Relationship?) {
        if (relationship?.member?.id != member?.id && relationship?.relatedMember?.id != member?.id) {
            throw ValidationException("해당 관계에 속한 회원이 아닙니다.")
        }
    }
}
