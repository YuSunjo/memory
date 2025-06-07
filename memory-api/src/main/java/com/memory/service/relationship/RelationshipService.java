package com.memory.service.relationship;

import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.relationship.Relationship;
import com.memory.domain.relationship.RelationshipStatus;
import com.memory.domain.relationship.repository.RelationshipRepository;
import com.memory.dto.relationship.RelationshipRequest;
import com.memory.dto.relationship.response.RelationshipListResponse;
import com.memory.dto.relationship.response.RelationshipResponse;
import com.memory.exception.customException.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public RelationshipResponse createRelationshipRequest(Long memberId, RelationshipRequest.Create createRequestDto) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("요청한 회원을 찾을 수 없습니다."));

        Member relatedMember = memberRepository.findMemberById(createRequestDto.getRelatedMemberId())
                .orElseThrow(() -> new NotFoundException("대상 회원을 찾을 수 없습니다."));

        Relationship relationship = Relationship.createRelationship(member, relatedMember, RelationshipStatus.PENDING);
        relationshipRepository.save(relationship);

        return RelationshipResponse.from(relationship);
    }

    @Transactional
    public RelationshipResponse acceptRelationshipRequest(Long memberId, Long relationshipId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("요청한 회원을 찾을 수 없습니다."));

        Relationship relationship = relationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new NotFoundException("관계 요청을 찾을 수 없습니다."));

        relationship.validateAcceptPermission(member);
        relationship.accept();

        Relationship reciprocalRelationship = Relationship.createRelationship(
                relationship.getRelatedMember(), 
                relationship.getMember(),
                RelationshipStatus.ACCEPTED
        );
        relationshipRepository.save(reciprocalRelationship);

        return RelationshipResponse.from(relationship);
    }

    @Transactional(readOnly = true)
    public RelationshipListResponse getRelationships(Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Relationship> relationships = relationshipRepository.findByMember(member);
        return RelationshipListResponse.fromEntities(relationships);
    }

    @Transactional(readOnly = true)
    public RelationshipListResponse getRelationshipsByStatus(Long memberId, RelationshipStatus status) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Relationship> relationships = relationshipRepository.findByMemberAndRelationshipStatus(member, status);
        return RelationshipListResponse.fromEntities(relationships);
    }

    @Transactional(readOnly = true)
    public RelationshipListResponse getReceivedRelationships(Long memberId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Relationship> relationships = relationshipRepository.findByRelatedMember(member);
        return RelationshipListResponse.fromEntities(relationships);
    }

    @Transactional(readOnly = true)
    public RelationshipListResponse getReceivedRelationshipsByStatus(Long memberId, RelationshipStatus status) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<Relationship> relationships = relationshipRepository.findByRelatedMemberAndRelationshipStatus(member, status);
        return RelationshipListResponse.fromEntities(relationships);
    }

    @Transactional
    public RelationshipResponse endRelationship(Long memberId, Long relationshipId) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("요청한 회원을 찾을 수 없습니다."));

        Relationship relationship = relationshipRepository.findById(relationshipId)
                .orElseThrow(() -> new NotFoundException("관계를 찾을 수 없습니다."));

        relationship.validateEndPermission(member);
        relationship.end();

        List<Relationship> reciprocalRelationships = relationshipRepository.findByMemberIdAndRelatedMemberId(
                relationship.getRelatedMember().getId(),
                relationship.getMember().getId()
        );

        for (Relationship reciprocalRelationship : reciprocalRelationships) {
            if (reciprocalRelationship.getRelationshipStatus() == RelationshipStatus.ACCEPTED) {
                reciprocalRelationship.end();
            }
        }

        return RelationshipResponse.from(relationship);
    }

}
