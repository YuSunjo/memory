package com.memory.dto.search;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class MemorySearchResponse {

    private Long memoryId;
    private String title;
    private String content;
    private String locationName;
    private LocalDate memorableDate;
    private String memorableDateText;
    private String memoryType;
    private List<String> hashtags;
    
    // 메모리 작성자 정보
    private Long memberId;
    private String memberName;
    private String memberNickname;
    private String memberEmail;
    private String memberFileUrl;
    
    // 관계된 멤버 정보
    private Long relationshipMemberId;
    private String relationshipMemberName;
    private String relationshipMemberNickname;
    private String relationshipMemberEmail;
    private String relationshipMemberFileUrl;
    
    private HighlightInfo highlights;

    @Getter
    @Builder
    public static class HighlightInfo {
        private List<String> title;
        private List<String> content;
        private List<String> locationName;
        private List<String> hashtags;
        private List<String> memberName;
        private List<String> memberNickname;
        private List<String> relationshipMemberName;
        private List<String> relationshipMemberNickname;
    }
}