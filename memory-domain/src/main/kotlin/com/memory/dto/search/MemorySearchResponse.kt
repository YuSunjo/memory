package com.memory.dto.search

import java.time.LocalDate

data class MemorySearchResponse(
    val memoryId: Long,
    val title: String,
    val content: String?,
    val locationName: String?,
    val memorableDate: LocalDate?,
    val memorableDateText: String?,
    val memoryType: String,
    val hashtags: List<String>?,

    // 메모리 작성자 정보
    val memberId: Long,
    val memberName: String,
    val memberNickname: String,
    val memberEmail: String,
    val memberFileUrl: String?,

    // 관계된 멤버 정보
    val relationshipMemberId: Long?,
    val relationshipMemberName: String?,
    val relationshipMemberNickname: String?,
    val relationshipMemberEmail: String?,
    val relationshipMemberFileUrl: String?,

    val highlights: HighlightInfo?
) {
    data class HighlightInfo(
        val title: List<String>?,
        val content: List<String>?,
        val locationName: List<String>?,
        val hashtags: List<String>?,
        val memberName: List<String>?,
        val memberNickname: List<String>?,
        val relationshipMemberName: List<String>?,
        val relationshipMemberNickname: List<String>?
    )
}
