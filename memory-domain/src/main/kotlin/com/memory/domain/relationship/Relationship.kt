package com.memory.domain.relationship

import com.memory.domain.BaseTimeEntity
import com.memory.domain.member.Member
import com.memory.exception.customException.ValidationException
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "relationship")
class Relationship(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_member_id")
    var relatedMember: Member,

    @Enumerated(EnumType.STRING)
    var relationshipStatus: RelationshipStatus,

    var startDate: LocalDateTime = LocalDateTime.now(),

    var endDate: LocalDateTime? = null
) : BaseTimeEntity() {

    companion object {
        @JvmStatic
        fun createRelationship(
            member: Member,
            relatedMember: Member,
            relationshipStatus: RelationshipStatus
        ): Relationship {
            return Relationship(
                member = member,
                relatedMember = relatedMember,
                relationshipStatus = relationshipStatus
            )
        }
    }

    fun validateAcceptPermission(member: Member) {
        if (this.relatedMember.id != member.id) {
            throw ValidationException("관계 요청을 수락할 권한이 없습니다.")
        }
    }

    fun validateEndPermission(member: Member) {
        if (this.member.id != member.id && this.relatedMember.id != member.id) {
            throw ValidationException("관계를 끊을 권한이 없습니다.")
        }
    }

    fun accept() {
        if (this.relationshipStatus != RelationshipStatus.PENDING) {
            throw ValidationException("Only pending relationships can be accepted")
        }
        this.relationshipStatus = RelationshipStatus.ACCEPTED
    }

    fun end() {
        if (this.relationshipStatus != RelationshipStatus.ACCEPTED) {
            throw ValidationException("Only accepted relationships can be ended")
        }
        this.relationshipStatus = RelationshipStatus.ENDED
        this.endDate = LocalDateTime.now()
    }
}
