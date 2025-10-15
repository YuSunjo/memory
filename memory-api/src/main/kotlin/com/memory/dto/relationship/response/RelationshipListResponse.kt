package com.memory.dto.relationship.response

import com.memory.domain.relationship.Relationship
import java.util.stream.Collectors

data class RelationshipListResponse(
    val relationships: MutableList<RelationshipResponse?>?
) {
    companion object {
        @JvmStatic
        fun fromEntities(relationships: List<Relationship>): RelationshipListResponse {
            return RelationshipListResponse(
                relationships.stream()
                    .map { relationship: Relationship -> RelationshipResponse.from(relationship) }
                    .collect(Collectors.toList())
            )
        }
    }
}
