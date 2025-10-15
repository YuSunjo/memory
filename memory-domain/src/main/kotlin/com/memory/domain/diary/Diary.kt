package com.memory.domain.diary

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import jakarta.persistence.*
import java.time.LocalDate

@Entity
class Diary(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var title: String? = null,

    @Column(columnDefinition = "TEXT")
    var content: String? = null,

    var date: LocalDate? = null,

    var mood: String? = null,

    var weather: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,
) : BaseTimeEntity() {

    companion object {
        @JvmStatic
        fun create(
            title: String,
            content: String?,
            date: LocalDate,
            mood: String?,
            weather: String?,
            member: Member
        ): Diary = Diary(
            title = title,
            content = content,
            date = date,
            mood = mood,
            weather = weather,
            member = member
        )
    }

    fun update(
        title: String,
        content: String,
        date: LocalDate,
        mood: String?,
        weather: String?
    ) {
        this.title = title
        this.content = content
        this.date = date
        this.mood = mood
        this.weather = weather
    }

    fun isOwner(member: Member): Boolean =
        this.member?.id != member.id
}