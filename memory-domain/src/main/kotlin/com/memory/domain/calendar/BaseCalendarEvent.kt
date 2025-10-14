package com.memory.domain.calendar

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "event_type")
abstract class BaseCalendarEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var title: String,

    var description: String?,

    var startDateTime: LocalDateTime,

    var endDateTime: LocalDateTime?,

    var location: String?,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member
) : BaseTimeEntity() {

    fun update(
        title: String,
        description: String?,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime?,
        location: String?
    ) {
        this.title = title
        this.description = description
        this.startDateTime = startDateTime
        this.endDateTime = endDateTime
        this.location = location
    }

    fun isOwner(member: Member): Boolean {
        return this.member.id == member.id
    }

    abstract fun validateAccessPermission(member: Member)
}