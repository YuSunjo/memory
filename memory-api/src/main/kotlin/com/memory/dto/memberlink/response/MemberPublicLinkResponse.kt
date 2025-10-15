package com.memory.dto.memberlink.response

import com.memory.dto.member.response.MemberResponse

data class MemberPublicLinkResponse(
    val memberLinks: MutableList<MemberLinkResponse?>?,
    val member: MemberResponse?
) {
    companion object {
        @JvmStatic
        fun of(memberLinks: MutableList<MemberLinkResponse?>?, member: MemberResponse?): MemberPublicLinkResponse {
            return MemberPublicLinkResponse(memberLinks, member)
        }
    }
}
