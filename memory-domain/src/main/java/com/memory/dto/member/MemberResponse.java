package com.memory.dto.member;

import com.memory.domain.member.Member;

public record MemberResponse(
        Long id,
        String email,
        String name,
        String nickname,
        String profileImageUrl
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getNickname(),
                member.getProfileImageUrl()
        );
    }
}