package com.memory.dto.search;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AutocompleteSuggestion {
    
    private String text;                    // 제안 텍스트
    private SuggestionType type;           // 제안 타입 (TITLE, HASHTAG)
    private long matchCount;               // 매칭되는 메모리 개수
    private float score;                   // 관련도 점수
    
    public enum SuggestionType {
        TITLE,      // 제목에서 추출된 제안
        HASHTAG     // 해시태그에서 추출된 제안
    }
}