package com.memory.domain.routine

import com.memory.domain.BaseTimeEntity
import com.memory.domain.common.repeat.RepeatSetting
import com.memory.domain.member.Member
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class Routine(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    var title: String? = null,

    var content: String? = null,

    var active: Boolean = true, // 활성화 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member? = null,

    @Embedded
    var repeatSetting: RepeatSetting = RepeatSetting.none()
) : BaseTimeEntity() {

    companion object {
        @JvmStatic
        fun create(
            title: String,
            content: String,
            member: Member,
            repeatSetting: RepeatSetting?
        ): Routine = Routine(
            title = title,
            content = content,
            active = true,
            member = member,
            repeatSetting = repeatSetting ?: RepeatSetting.none()
        )
    }

    fun update(
        title: String,
        content: String,
        repeatSetting: RepeatSetting?
    ) {
        this.title = title
        this.content = content
        this.repeatSetting = repeatSetting ?: RepeatSetting.none()
    }

    fun activate() {
        this.active = true
    }

    fun deactivate() {
        this.active = false
    }

    fun isOwner(member: Member?): Boolean {
        val my = this.member ?: return false
        val other = member ?: return false
        return my.id == other.id
    }

    // 특정 날짜에 이 루틴이 적용되는지 확인
    fun isApplicableOn(dateTime: LocalDateTime): Boolean {
        if (!active) return false
        return repeatSetting.isApplicableOn(dateTime.toLocalDate())
    }

    // Todo로 변환할 때 사용 (기본 시간 사용)
    fun getDateTimeFor(targetDate: LocalDateTime): LocalDateTime =
        targetDate.toLocalDate().atTime(9, 0) // 기본값: 오전 9시
}