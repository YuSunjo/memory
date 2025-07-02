package com.memory.dto.memberlink;

import com.memory.domain.member.Member;
import com.memory.domain.memberlink.MemberLink;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

public class MemberLinkRequest {

    @Getter
    @NoArgsConstructor
    public static class Create {

        @NotBlank(message = "링크 제목은 필수입니다.")
        @Size(max = 100, message = "링크 제목은 100자를 초과할 수 없습니다.")
        private String title;

        @NotBlank(message = "링크 URL은 필수입니다.")
        @URL(message = "올바른 URL 형식이어야 합니다.")
        @Size(max = 500, message = "링크 URL은 500자를 초과할 수 없습니다.")
        private String url;

        @Size(max = 200, message = "링크 설명은 200자를 초과할 수 없습니다.")
        private String description;

        private Boolean isActive;

        private Boolean isVisible;

        @URL(message = "올바른 URL 형식이어야 합니다.")
        @Size(max = 500, message = "아이콘 URL은 500자를 초과할 수 없습니다.")
        private String iconUrl;

        public MemberLink toEntity(Member member, Integer displayOrder) {
            return MemberLink.builder()
                    .member(member)
                    .title(title)
                    .url(url)
                    .description(description)
                    .displayOrder(displayOrder)
                    .isActive(isActive != null ? isActive : true)
                    .isVisible(isVisible != null ? isVisible : true)
                    .iconUrl(iconUrl)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    public static class Update {

        @NotBlank(message = "링크 제목은 필수입니다.")
        @Size(max = 100, message = "링크 제목은 100자를 초과할 수 없습니다.")
        private String title;

        @NotBlank(message = "링크 URL은 필수입니다.")
        @URL(message = "올바른 URL 형식이어야 합니다.")
        @Size(max = 500, message = "링크 URL은 500자를 초과할 수 없습니다.")
        private String url;

        @Size(max = 200, message = "링크 설명은 200자를 초과할 수 없습니다.")
        private String description;

        @NotNull(message = "표시 순서는 필수입니다.")
        private Integer displayOrder;

        @NotNull(message = "활성 상태는 필수입니다.")
        private Boolean isActive;

        @NotNull(message = "공개 여부는 필수입니다.")
        private Boolean isVisible;

        @URL(message = "올바른 URL 형식이어야 합니다.")
        @Size(max = 500, message = "아이콘 URL은 500자를 초과할 수 없습니다.")
        private String iconUrl;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateOrder {

        @NotNull(message = "표시 순서는 필수입니다.")
        private Integer displayOrder;
    }
}
