package com.memory.domain.calendar

import com.memory.domain.member.Member
import com.memory.domain.relationship.Relationship
import com.memory.exception.customException.ValidationException
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("ANNIVERSARY_EVENT")
class AnniversaryEvent(
    title: String,
    description: String?,
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime?,
    location: String?,
    member: Member,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_id")
    var relationship: Relationship,

    var isDday: Boolean = false
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
            relationship: Relationship,
            isDday: Boolean = false
        ): AnniversaryEvent {
            return AnniversaryEvent(
                title = title,
                description = description,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                location = location,
                member = member,
                relationship = relationship,
                isDday = isDday
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

    fun update(
        title: String,
        description: String?,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
        location: String?,
        isDday: Boolean
    ) {
        super.update(title, description, startDateTime, endDateTime, location)
        this.isDday = isDday
    }
}
