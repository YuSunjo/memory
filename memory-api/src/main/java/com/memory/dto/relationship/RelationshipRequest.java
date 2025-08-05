package com.memory.dto.relationship;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

public class RelationshipRequest {

    @Getter
    public static class Create {
        @NotNull(message = "대상 회원 ID는 필수 입력값입니다.")
        private final Long relatedMemberId;

        public Create(Long relatedMemberId) {
            this.relatedMemberId = relatedMemberId;
        }
    }
}