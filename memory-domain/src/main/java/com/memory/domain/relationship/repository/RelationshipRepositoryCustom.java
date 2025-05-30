package com.memory.domain.relationship.repository;

import com.memory.domain.member.Member;
import com.memory.domain.relationship.Relationship;
import com.memory.domain.relationship.RelationshipStatus;

import java.util.List;

public interface RelationshipRepositoryCustom {
    List<Relationship> findByMember(Member member);
    List<Relationship> findByMemberAndRelationshipStatus(Member member, RelationshipStatus status);
    List<Relationship> findByMemberIdAndRelatedMemberId(Long memberId, Long relatedMemberId);
}
