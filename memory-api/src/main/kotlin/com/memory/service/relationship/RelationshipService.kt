package com.memory.service.relationship

import com.memory.domain.member.repository.MemberRepository
import com.memory.domain.relationship.Relationship
import com.memory.domain.relationship.Relationship.Companion.createRelationship
import com.memory.domain.relationship.RelationshipStatus
import com.memory.domain.relationship.repository.RelationshipRepository
import com.memory.dto.relationship.RelationshipRequest
import com.memory.dto.relationship.response.RelationshipListResponse
import com.memory.dto.relationship.response.RelationshipResponse
import com.memory.dto.relationship.response.RelationshipResponse.Companion.from
import com.memory.exception.customException.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class RelationshipService(
    private val relationshipRepository: RelationshipRepository,
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun createRelationshipRequest(memberId: Long?, createRequestDto: RelationshipRequest.Create): RelationshipResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("요청한 회원을 찾을 수 없습니다.") }

        val relatedMember = memberRepository.findMemberById(createRequestDto.relatedMemberId)
            .orElseThrow { NotFoundException("대상 회원을 찾을 수 없습니다.") }

        val relationship = createRelationship(member, relatedMember, RelationshipStatus.PENDING)
        relationshipRepository.save<Relationship?>(relationship)

        return from(relationship)
    }

    @Transactional
    fun acceptRelationshipRequest(memberId: Long?, relationshipId: Long): RelationshipResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("요청한 회원을 찾을 수 없습니다.") }

        val relationship = relationshipRepository.findById(relationshipId)
            .orElseThrow { NotFoundException("관계 요청을 찾을 수 없습니다.") }

        relationship.validateAcceptPermission(member)
        relationship.accept()

        val reciprocalRelationship = createRelationship(
            relationship.relatedMember,
            relationship.member,
            RelationshipStatus.ACCEPTED
        )
        relationshipRepository.save<Relationship?>(reciprocalRelationship)

        return from(relationship)
    }

    @Transactional(readOnly = true)
    fun getRelationships(memberId: Long?): RelationshipListResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val relationships: List<Relationship> = relationshipRepository.findByMember(member)
        return RelationshipListResponse.fromEntities(relationships)
    }

    @Transactional(readOnly = true)
    fun getRelationshipsByStatus(memberId: Long?, status: RelationshipStatus?): RelationshipListResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val relationships: List<Relationship> =
            relationshipRepository.findByMemberAndRelationshipStatus(member, status)
        return RelationshipListResponse.fromEntities(relationships)
    }

    @Transactional(readOnly = true)
    fun getReceivedRelationships(memberId: Long?): RelationshipListResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val relationships: List<Relationship> = relationshipRepository.findByRelatedMember(member)
        return RelationshipListResponse.fromEntities(relationships)
    }

    @Transactional(readOnly = true)
    fun getReceivedRelationshipsByStatus(memberId: Long?, status: RelationshipStatus?): RelationshipListResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val relationships: List<Relationship> =
            relationshipRepository.findByRelatedMemberAndRelationshipStatus(member, status)
        return RelationshipListResponse.fromEntities(relationships)
    }

    @Transactional
    fun endRelationship(memberId: Long?, relationshipId: Long): RelationshipResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("요청한 회원을 찾을 수 없습니다.") }

        val relationship = relationshipRepository.findById(relationshipId)
            .orElseThrow { NotFoundException("관계를 찾을 수 없습니다.") }

        relationship.validateEndPermission(member)
        relationship.end()

        val reciprocalRelationships: List<Relationship> =
            relationshipRepository.findByMemberIdAndRelatedMemberId(
                relationship.relatedMember.id,
                relationship.member.id
            )

        for (reciprocalRelationship in reciprocalRelationships) {
            if (reciprocalRelationship.relationshipStatus == RelationshipStatus.ACCEPTED) {
                reciprocalRelationship.end()
            }
        }

        return from(relationship)
    }
}
