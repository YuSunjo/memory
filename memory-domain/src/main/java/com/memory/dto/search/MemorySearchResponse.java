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
    private Long memberId;
    private Long relationshipMemberId;
    private HighlightInfo highlights;

    @Getter
    @Builder
    public static class HighlightInfo {
        private List<String> title;
        private List<String> content;
        private List<String> locationName;
        private List<String> hashtags;
    }
}