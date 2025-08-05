package com.memory.dto.member.response;

import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.dto.file.response.FileResponse;

public record MemberResponse(
        Long id,
        String email,
        String name,
        String nickname,
        MemberType memberType,
        FileResponse profile
) {

    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getId(),
                member.getEmail(),
                member.getName(),
                member.getNickname(),
                member.getMemberType(),
                member.getFile() != null ? FileResponse.from(member.getFile()) : null
        );
    }
}
