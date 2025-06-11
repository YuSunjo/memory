package com.memory.dto.member.response;

import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;

public record MemberResponse(
        Long id,
        String email,
        String name,
        String nickname,
        String profileImageUrl,
        MemberType memberType
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getNickname(),
                null,
                member.getMemberType()
        );
    }
}