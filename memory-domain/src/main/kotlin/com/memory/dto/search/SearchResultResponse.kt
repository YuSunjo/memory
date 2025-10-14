package com.memory.dto.search

import java.time.LocalDate

data class SearchResultResponse(
    val memories: List<MemorySearchResponse>,
    val pageInfo: PageInfo,
    val metadata: SearchMetadata
) {
    data class PageInfo(
        val currentPage: Int,
        val totalPages: Int,
        val pageSize: Int,
        val totalElements: Long,
        val hasNext: Boolean,
        val hasPrevious: Boolean
    )

    data class SearchMetadata(
        val searchType: SearchType,
        val query: String?,
        val hashtags: List<String>?,
        val fromDate: LocalDate?,
        val toDate: LocalDate?,
        val searchTimeMs: Long
    )
}
