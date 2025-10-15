package com.memory.dto.relationship.response

import com.memory.domain.relationship.Relationship
import com.memory.domain.relationship.RelationshipStatus
import com.memory.dto.member.response.MemberResponse
import com.memory.dto.member.response.MemberResponse.Companion.from
import java.time.LocalDateTime

data class RelationshipResponse(
    val id: Long?,
    val member: MemberResponse?,
    val relatedMember: MemberResponse?,
    val relationshipStatus: RelationshipStatus?,
    val startDate: LocalDateTime?,
    val endDate: LocalDateTime?
) {
    companion object {
        @JvmStatic
        fun from(relationship: Relationship): RelationshipResponse {
            return RelationshipResponse(
                relationship.id,
                from(relationship.member),
                from(relationship.relatedMember),
                relationship.relationshipStatus,
                relationship.startDate,
                relationship.endDate
            )
        }
    }
}