package com.memory.dto.search

data class AutocompleteSuggestion(
    val text: String,                    // 제안 텍스트
    val type: SuggestionType,           // 제안 타입 (TITLE, HASHTAG)
    val matchCount: Long,               // 매칭되는 메모리 개수
    val score: Float                    // 관련도 점수
) {
    enum class SuggestionType {
        TITLE,      // 제목에서 추출된 제안
        HASHTAG     // 해시태그에서 추출된 제안
    }
}
