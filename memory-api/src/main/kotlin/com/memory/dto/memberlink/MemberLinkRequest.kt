package com.memory.dto.memberlink

import com.memory.domain.member.Member
import com.memory.domain.memberlink.MemberLink
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.hibernate.validator.constraints.URL

class MemberLinkRequest {

    data class Create(
        @field:NotBlank(message = "링크 제목은 필수입니다.")
        @field:Size(max = 100, message = "링크 제목은 100자를 초과할 수 없습니다.")
        var title: String? = null,

        @field:NotBlank(message = "링크 URL은 필수입니다.")
        @field:URL(message = "올바른 URL 형식이어야 합니다.")
        @field:Size(max = 500, message = "링크 URL은 500자를 초과할 수 없습니다.")
        var url: String? = null,

        @field:Size(max = 200, message = "링크 설명은 200자를 초과할 수 없습니다.")
        var description: String? = null,

        var isActive: Boolean? = null,
        var isVisible: Boolean? = null,

        @field:URL(message = "올바른 URL 형식이어야 합니다.")
        @field:Size(max = 500, message = "아이콘 URL은 500자를 초과할 수 없습니다.")
        var iconUrl: String? = null
    ) {
        fun toEntity(member: Member, displayOrder: Int): MemberLink =
            MemberLink.create(
                member = member,
                title = requireNotNull(title) { "title must not be null" },
                url = requireNotNull(url) { "url must not be null" },
                description = description,
                displayOrder = displayOrder,
                isActive = isActive ?: true,
                isVisible = isVisible ?: true,
                iconUrl = iconUrl
            )
    }

    data class Update(
        @field:NotBlank(message = "링크 제목은 필수입니다.")
        @field:Size(max = 100, message = "링크 제목은 100자를 초과할 수 없습니다.")
        var title: String? = null,

        @field:NotBlank(message = "링크 URL은 필수입니다.")
        @field:URL(message = "올바른 URL 형식이어야 합니다.")
        @field:Size(max = 500, message = "링크 URL은 500자를 초과할 수 없습니다.")
        var url: String? = null,

        @field:Size(max = 200, message = "링크 설명은 200자를 초과할 수 없습니다.")
        var description: String? = null,

        @field:NotNull(message = "표시 순서는 필수입니다.")
        var displayOrder: Int? = null,

        @field:NotNull(message = "활성 상태는 필수입니다.")
        var isActive: Boolean? = null,

        @field:NotNull(message = "공개 여부는 필수입니다.")
        var isVisible: Boolean? = null,

        @field:URL(message = "올바른 URL 형식이어야 합니다.")
        @field:Size(max = 500, message = "아이콘 URL은 500자를 초과할 수 없습니다.")
        var iconUrl: String? = null
    )

    data class UpdateOrder(
        @field:NotNull(message = "표시 순서는 필수입니다.")
        var displayOrder: Int? = null
    )
}