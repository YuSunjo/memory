package com.memory.dto.relationship.response;

import com.memory.domain.relationship.Relationship;
import com.memory.domain.relationship.RelationshipStatus;

import java.time.LocalDateTime;

public record RelationshipResponse(
        Long id,
        Long memberId,
        Long relatedMemberId,
        RelationshipStatus relationshipStatus,
        LocalDateTime startDate,
        LocalDateTime endDate
) {

    public static RelationshipResponse from(Relationship relationship) {
        return new RelationshipResponse(
                relationship.getId(),
                relationship.getMember().getId(),
                relationship.getRelatedMember().getId(),
                relationship.getRelationshipStatus(),
                relationship.getStartDate(),
                relationship.getEndDate()
        );
    }
}