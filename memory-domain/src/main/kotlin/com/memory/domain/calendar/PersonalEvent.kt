package com.memory.domain.calendar

import com.memory.domain.member.Member
import com.memory.exception.customException.ValidationException
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Entity
import java.time.LocalDateTime

@Entity
@DiscriminatorValue("PERSONAL")
class PersonalEvent(
    title: String,
    description: String?,
    startDateTime: LocalDateTime,
    endDateTime: LocalDateTime?,
    location: String?,
    member: Member
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
            member: Member
        ): PersonalEvent {
            return PersonalEvent(
                title = title,
                description = description,
                startDateTime = startDateTime,
                endDateTime = endDateTime,
                location = location,
                member = member
            )
        }
    }

    override fun validateAccessPermission(member: Member) {
        if (!isOwner(member)) {
            throw ValidationException("이 일정에 접근할 권한이 없습니다.")
        }
    }
}
