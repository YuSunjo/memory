package com.memory.dto.relationship

import jakarta.validation.constraints.NotNull
import lombok.Getter

class RelationshipRequest {
    @Getter
    class Create(

        @field:NotNull(message = "대상 회원 ID는 필수 입력값입니다.")
        val relatedMemberId: Long?
    )
}