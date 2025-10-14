package com.memory.domain.memberlink

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime

@Entity
class MemberLink(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    val member: Member,

    var title: String,

    var url: String,

    var description: String? = null,

    @Column(name = "display_order")
    var displayOrder: Int = 1,

    var isActive: Boolean = true,

    var isVisible: Boolean = true,

    @Column(length = 500)
    var iconUrl: String? = null,

    var clickCount : Long = 0L,

    var lastClickedAt : LocalDateTime? = null,

    ): BaseTimeEntity() {

    companion object {
        @JvmStatic
        fun create(
            member: Member, title: String, url: String, description: String?,
            displayOrder: Int, isActive: Boolean, isVisible: Boolean,
            iconUrl: String?
        ): MemberLink {
            return MemberLink(
                member = member,
                title = title,
                url = url,
                description = description,
                displayOrder = displayOrder,
                isActive = isActive,
                isVisible = isVisible,
                iconUrl = iconUrl
            )
        }
    }

    fun update(
        title: String?, url: String?, description: String?,
        displayOrder: Int, isActive: Boolean, isVisible: Boolean,
        iconUrl: String?
    ) {
        this.title = title!!
        this.url = url!!
        this.description = description
        this.displayOrder = displayOrder
        this.isActive = isActive
        this.isVisible = isVisible
        this.iconUrl = iconUrl
    }

    fun updateDisplayOrder(displayOrder: Int) {
        this.displayOrder = displayOrder
    }

    fun isSameOrder(targetOrder: Int): Boolean {
        return this.displayOrder == targetOrder
    }

    fun incrementClickCount() {
        this.clickCount++
        this.lastClickedAt = LocalDateTime.now()
    }

    fun isAccessible(): Boolean {
        return this.isActive && this.isVisible
    }
}