package com.memory.domain.calendar

import com.memory.domain.member.Member
import com.memory.domain.relationship.Relationship
import com.memory.exception.customException.ValidationException
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("RELATIONSHIP_EVENT")
class RelationshipEvent(
    title: String,
    description: String?,
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime?,
    location: String?,
    member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_id")
    var relationship: Relationship
) : BaseCalendarEvent(
    title = title,
    description = description,
    startDateTime = startDateTime,
    endDateTime = endDateTime,
    location = location,
    member = member
) {

    companion object {
        @JvmStatic
        fun create(
            title: String,
            description: String?,
            startDateTime: LocalDateTime,
            endDateTime: LocalDateTime?,
            location: String?,
            member: Member,
            relationship: Relationship
        ): RelationshipEvent {
            return RelationshipEvent(
                title = title,
                description = description,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                location = location,
                member = member,
                relationship = relationship
            )
        }
    }

    fun isRelatedTo(member: Member): Boolean {
        return relationship.relatedMember.id == member.id ||
                relationship.member.id == member.id
    }

    override fun validateAccessPermission(member: Member) {
        if (!isOwner(member) && !isRelatedTo(member)) {
            throw ValidationException("이 일정에 접근할 권한이 없습니다.")
        }
    }
}
