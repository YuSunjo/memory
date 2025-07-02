package com.memory.dto.memberlink.response;

import com.memory.domain.memberlink.MemberLink;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class MemberLinkResponse {

    private Long id;
    private String title;
    private String url;
    private String description;
    private Integer displayOrder;
    private Boolean isActive;
    private Boolean isVisible;
    private String iconUrl;
    private Long clickCount;
    private LocalDateTime createdAt;

    public static MemberLinkResponse from(MemberLink memberLink) {
        return new MemberLinkResponse(
                memberLink.getId(),
                memberLink.getTitle(),
                memberLink.getUrl(),
                memberLink.getDescription(),
                memberLink.getDisplayOrder(),
                memberLink.getIsActive(),
                memberLink.getIsVisible(),
                memberLink.getIconUrl(),
                memberLink.getClickCount(),
                memberLink.getCreateDate()
        );
    }

    // 공개용 응답 (통계 정보 제외)
    public static MemberLinkResponse forPublic(MemberLink memberLink) {
        return new MemberLinkResponse(
                memberLink.getId(),
                memberLink.getTitle(),
                memberLink.getUrl(),
                memberLink.getDescription(),
                memberLink.getDisplayOrder(),
                null,
                null,
                memberLink.getIconUrl(),
                null,
                null
        );
    }
}
