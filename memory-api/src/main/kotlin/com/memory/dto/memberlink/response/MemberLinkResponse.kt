package com.memory.dto.memberlink.response

import com.memory.domain.memberlink.MemberLink
import java.time.LocalDateTime

data class MemberLinkResponse(
    val id: Long?,
    val title: String,
    val url: String,
    val description: String?,
    val displayOrder: Int,
    val isActive: Boolean?,
    val isVisible: Boolean?,
    val iconUrl: String?,
    val clickCount: Long?,
    val createDate: LocalDateTime?
) {
    companion object {
        @JvmStatic
        fun from(memberLink: MemberLink): MemberLinkResponse {
            return MemberLinkResponse(
                memberLink.id,
                memberLink.title,
                memberLink.url,
                memberLink.description,
                memberLink.displayOrder,
                memberLink.isActive,
                memberLink.isVisible,
                memberLink.iconUrl,
                memberLink.clickCount,
                memberLink.createDate
            )
        }

        // 공개용 응답 (통계 정보 제외)
        @JvmStatic
        fun forPublic(memberLink: MemberLink): MemberLinkResponse {
            return MemberLinkResponse(
                memberLink.id,
                memberLink.title,
                memberLink.url,
                memberLink.description,
                memberLink.displayOrder,
                null,
                null,
                memberLink.iconUrl,
                null,
                null
            )
        }
    }
}
