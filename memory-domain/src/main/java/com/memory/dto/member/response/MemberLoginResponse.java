package com.memory.dto.member.response;

public record MemberLoginResponse(
        String accessToken,
        String refreshToken
) {
    public static MemberLoginResponse of(String accessToken, String refreshToken) {
        return new MemberLoginResponse(accessToken, refreshToken);
    }
}
