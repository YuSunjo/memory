package com.memory.dto.relationship.response;

import com.memory.domain.relationship.Relationship;
import com.memory.domain.relationship.RelationshipStatus;
import com.memory.dto.member.response.MemberResponse;

import java.time.LocalDateTime;

public record RelationshipResponse(
        Long id,
        MemberResponse member,
        MemberResponse relatedMember,
        RelationshipStatus relationshipStatus,
        LocalDateTime startDate,
        LocalDateTime endDate
) {

    public static RelationshipResponse from(Relationship relationship) {
        return new RelationshipResponse(
                relationship.getId(),
                MemberResponse.from(relationship.getMember()),
                MemberResponse.from(relationship.getRelatedMember()),
                relationship.getRelationshipStatus(),
                relationship.getStartDate(),
                relationship.getEndDate()
        );
    }
}