package com.memory.dto.member.response

import com.memory.domain.member.Member
import com.memory.domain.member.MemberType
import com.memory.dto.file.response.FileResponse

data class MemberResponse(
    val id: Long?,
    val email: String?,
    val name: String?,
    val nickname: String?,
    val memberType: MemberType?,
    val profile: FileResponse?
) {
    companion object {
        @JvmStatic
        fun from(member: Member): MemberResponse {
            return MemberResponse(
                member.id,
                member.email,
                member.name,
                member.nickname,
                member.memberType,
                if (member.file != null) FileResponse.from(member.file!!) else null
            )
        }
    }
}
