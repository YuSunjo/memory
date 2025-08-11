package com.memory.document.memory;

import java.util.List;

public record RelationshipInfo(
    List<RelatedMemberInfo> relationships
) {
    public record RelatedMemberInfo(
        Long id,
        String name,
        String nickname,
        String email,
        String profileFileUrl
    ) {}
}