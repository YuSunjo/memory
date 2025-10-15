package com.memory.dto.member.response

data class MemberLoginResponse(
    val accessToken: String?,
    val refreshToken: String?
) {
    companion object {
        @JvmStatic
        fun of(accessToken: String?, refreshToken: String?): MemberLoginResponse {
            return MemberLoginResponse(accessToken, refreshToken)
        }
    }
}
