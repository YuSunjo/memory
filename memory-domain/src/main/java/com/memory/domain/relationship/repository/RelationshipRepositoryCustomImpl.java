package com.memory.domain.relationship.repository;

import com.memory.domain.member.Member;
import com.memory.domain.relationship.Relationship;
import com.memory.domain.relationship.RelationshipStatus;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.memory.domain.relationship.QRelationship.relationship;

@RequiredArgsConstructor
public class RelationshipRepositoryCustomImpl implements RelationshipRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Relationship> findByMember(Member member) {
        return queryFactory.selectFrom(relationship)
                .where(
                    relationship.member.eq(member),
                    relationship.deleteDate.isNull()
                )
                .fetch();
    }

    @Override
    public List<Relationship> findByMemberAndRelationshipStatus(Member member, RelationshipStatus status) {
        return queryFactory.selectFrom(relationship)
                .where(
                    relationship.member.eq(member),
                    relationship.relationshipStatus.eq(status),
                    relationship.deleteDate.isNull()
                )
                .fetch();
    }

    @Override
    public List<Relationship> findByMemberIdAndRelatedMemberId(Long memberId, Long relatedMemberId) {
        return queryFactory.selectFrom(relationship)
                .where(
                    relationship.member.id.eq(memberId),
                    relationship.relatedMember.id.eq(relatedMemberId),
                    relationship.deleteDate.isNull()
                )
                .fetch();
    }

    @Override
    public List<Relationship> findByRelatedMember(Member relatedMember) {
        return queryFactory.selectFrom(relationship)
                .where(
                    relationship.relatedMember.eq(relatedMember),
                    relationship.deleteDate.isNull()
                )
                .fetch();
    }

    @Override
    public List<Relationship> findByRelatedMemberAndRelationshipStatus(Member relatedMember, RelationshipStatus status) {
        return queryFactory.selectFrom(relationship)
                .where(
                    relationship.relatedMember.eq(relatedMember),
                    relationship.relationshipStatus.eq(status),
                    relationship.deleteDate.isNull()
                )
                .fetch();
    }

    @Override
    public List<Relationship> findByMemberOrRelatedMemberAndStatus(Member member, RelationshipStatus relationshipStatus) {
        return queryFactory.selectFrom(relationship)
                .where(
                    (relationship.member.eq(member).or(relationship.relatedMember.eq(member))),
                    relationship.relationshipStatus.eq(relationshipStatus),
                    relationship.deleteDate.isNull()
                )
                .fetch();
    }
}
