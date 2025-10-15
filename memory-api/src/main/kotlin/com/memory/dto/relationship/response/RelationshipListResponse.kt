package com.memory.dto.relationship.response;

import com.memory.domain.relationship.Relationship;

import java.util.List;
import java.util.stream.Collectors;

public record RelationshipListResponse(
        List<RelationshipResponse> relationships
) {
    public static RelationshipListResponse fromEntities(List<Relationship> relationships) {
        List<RelationshipResponse> relationshipResponses = relationships.stream()
                .map(RelationshipResponse::from)
                .collect(Collectors.toList());
        return new RelationshipListResponse(relationshipResponses);
    }

}
