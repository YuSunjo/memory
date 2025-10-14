package com.memory.domain.relationship.repository

import com.memory.domain.member.Member
import com.memory.domain.relationship.Relationship
import com.memory.domain.relationship.RelationshipStatus

interface RelationshipRepositoryCustom {
    fun findByMember(member: Member?): List<Relationship>
    fun findByMemberAndRelationshipStatus(member: Member?, status: RelationshipStatus?): List<Relationship>
    fun findByMemberIdAndRelatedMemberId(memberId: Long?, relatedMemberId: Long?): List<Relationship>
    fun findByRelatedMember(relatedMember: Member?): List<Relationship>
    fun findByRelatedMemberAndRelationshipStatus(relatedMember: Member?, status: RelationshipStatus?): List<Relationship>

    fun findByMemberOrRelatedMemberAndStatus(member: Member?, relationshipStatus: RelationshipStatus?): List<Relationship>
}
