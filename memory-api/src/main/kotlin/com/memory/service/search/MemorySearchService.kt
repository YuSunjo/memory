package com.memory.service.search

import com.memory.document.memory.MemoryDocument
import com.memory.document.memory.MemoryDocumentRepository
import com.memory.dto.search.AutocompleteResponse
import com.memory.dto.search.AutocompleteSuggestion
import com.memory.dto.search.MemorySearchRequest
import com.memory.dto.search.MemorySearchResponse
import com.memory.dto.search.SearchResultResponse
import com.memory.exception.customException.ValidationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.SearchHit
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class MemorySearchService(
    private val memoryDocumentRepository: MemoryDocumentRepository
) {

    /**
     * 게스트 사용자의 PUBLIC 메모리 검색
     */
    fun searchPublic(request: MemorySearchRequest): SearchResultResponse {
        val startTime = System.currentTimeMillis()

        validateRequest(request)

        val pageable: Pageable = PageRequest.of(request.page, request.size)
        val searchHits: Page<SearchHit<MemoryDocument>> = executePublicSearch(request, pageable)

        val memories = searchHits.content.stream()
            .map { hit -> convertToResponse(hit, request.highlight) }
            .toList()

        val searchTime = System.currentTimeMillis() - startTime

        return SearchResultResponse(
            memories = memories,
            pageInfo = buildPageInfo(searchHits),
            metadata = buildMetadata(request, searchTime)
        )
    }

    /**
     * 인증된 사용자의 메모리 검색 (본인 메모리 + 관계된 사용자 메모리 + PUBLIC 메모리)
     */
    fun searchAuthenticated(request: MemorySearchRequest, memberId: Long): SearchResultResponse {
        val startTime = System.currentTimeMillis()

        validateRequest(request)

        val pageable: Pageable = PageRequest.of(request.page, request.size)
        val searchHits: Page<SearchHit<MemoryDocument>> = executeAuthenticatedSearch(request, memberId, pageable)

        val memories = searchHits.content.stream()
            .map { hit -> convertToResponse(hit, request.highlight) }
            .toList()

        val searchTime = System.currentTimeMillis() - startTime

        return SearchResultResponse(
            memories = memories,
            pageInfo = buildPageInfo(searchHits),
            metadata = buildMetadata(request, searchTime)
        )
    }

    private fun validateRequest(request: MemorySearchRequest) {
        when (request.type) {
            com.memory.dto.search.SearchType.ALL,
            com.memory.dto.search.SearchType.TITLE,
            com.memory.dto.search.SearchType.CONTENT,
            com.memory.dto.search.SearchType.LOCATION -> {
                if (!StringUtils.hasText(request.query)) {
                    throw ValidationException("Query is required for ${request.type} search")
                }
            }
            com.memory.dto.search.SearchType.HASHTAGS -> {
                val hashtags = request.hashtags
                if (hashtags == null || hashtags.isEmpty()) {
                    throw ValidationException("Hashtags are required for HASHTAGS search")
                }
            }
            com.memory.dto.search.SearchType.DATE -> {
                val fromDate = request.fromDate
                val toDate = request.toDate
                if (fromDate == null || toDate == null) {
                    throw ValidationException("FromDate and ToDate are required for DATE search")
                }
                if (fromDate.isAfter(toDate)) {
                    throw ValidationException("FromDate must be before or equal to ToDate")
                }
            }
        }
    }

    private fun executePublicSearch(request: MemorySearchRequest, pageable: Pageable): Page<SearchHit<MemoryDocument>> {
        return when (request.type) {
            com.memory.dto.search.SearchType.ALL -> memoryDocumentRepository.searchPublicByAllFields(request.query, pageable)
            com.memory.dto.search.SearchType.TITLE -> memoryDocumentRepository.searchPublicByTitle(request.query, pageable)
            com.memory.dto.search.SearchType.CONTENT -> memoryDocumentRepository.searchPublicByContent(request.query, pageable)
            com.memory.dto.search.SearchType.HASHTAGS -> {
                val hashtags = request.hashtags!!
                memoryDocumentRepository.searchPublicByHashtags(hashtags, pageable)
            }
            com.memory.dto.search.SearchType.LOCATION -> memoryDocumentRepository.searchPublicByLocation(request.query, pageable)
            com.memory.dto.search.SearchType.DATE -> {
                val fromDate = request.fromDate!!
                val toDate = request.toDate!!
                memoryDocumentRepository.searchPublicByDateRange(fromDate, toDate, pageable)
            }
        }
    }

    private fun executeAuthenticatedSearch(request: MemorySearchRequest, memberId: Long, pageable: Pageable): Page<SearchHit<MemoryDocument>> {
        return when (request.type) {
            com.memory.dto.search.SearchType.ALL -> memoryDocumentRepository.searchByMemberAndAllFields(memberId, request.query, pageable)
            com.memory.dto.search.SearchType.TITLE -> memoryDocumentRepository.searchByMemberAndTitle(memberId, request.query, pageable)
            com.memory.dto.search.SearchType.CONTENT -> memoryDocumentRepository.searchByMemberAndContent(memberId, request.query, pageable)
            com.memory.dto.search.SearchType.HASHTAGS -> {
                val hashtags = request.hashtags!!
                memoryDocumentRepository.searchByMemberAndHashtags(memberId, hashtags, pageable)
            }
            com.memory.dto.search.SearchType.LOCATION -> memoryDocumentRepository.searchByMemberAndLocation(memberId, request.query, pageable)
            com.memory.dto.search.SearchType.DATE -> {
                val fromDate = request.fromDate!!
                val toDate = request.toDate!!
                memoryDocumentRepository.searchByMemberAndDateRange(memberId, fromDate, toDate, pageable)
            }
        }
    }

    private fun convertToResponse(hit: SearchHit<MemoryDocument>, includeHighlight: Boolean): MemorySearchResponse {
        val document = hit.content

        return MemorySearchResponse(
            memoryId = document.memoryId ?: 0L,
            title = document.title ?: "",
            content = document.content,
            locationName = document.locationName,
            memorableDate = document.memorableDate,
            memorableDateText = document.memorableDateText,
            memoryType = document.memoryType ?: "",
            hashtags = document.hashTags,

            // 메모리 작성자 정보
            memberId = document.memberId ?: 0L,
            memberName = document.memberName ?: "",
            memberNickname = document.memberNickname ?: "",
            memberEmail = document.memberEmail ?: "",
            memberFileUrl = document.memberFileUrl,

            // 관계된 멤버 정보
            relationshipMemberId = document.relationshipMemberId,
            relationshipMemberName = document.relationshipMemberName,
            relationshipMemberNickname = document.relationshipMemberNickname,
            relationshipMemberEmail = document.relationshipMemberEmail,
            relationshipMemberFileUrl = document.relationshipMemberFileUrl,

            highlights = if (includeHighlight && hit.highlightFields.isNotEmpty()) {
                buildHighlights(hit.highlightFields)
            } else null
        )
    }

    private fun buildHighlights(highlightFields: Map<String, List<String>>): MemorySearchResponse.HighlightInfo {
        return MemorySearchResponse.HighlightInfo(
            title = highlightFields["title"],
            content = highlightFields["content"],
            locationName = highlightFields["locationName"],
            hashtags = highlightFields["hashTags"],
            memberName = highlightFields["memberName"],
            memberNickname = highlightFields["memberNickname"],
            relationshipMemberName = highlightFields["relationshipMemberName"],
            relationshipMemberNickname = highlightFields["relationshipMemberNickname"]
        )
    }

    private fun buildPageInfo(searchHits: Page<SearchHit<MemoryDocument>>): SearchResultResponse.PageInfo {
        return SearchResultResponse.PageInfo(
            currentPage = searchHits.number,
            totalPages = searchHits.totalPages,
            pageSize = searchHits.size,
            totalElements = searchHits.totalElements,
            hasNext = searchHits.hasNext(),
            hasPrevious = searchHits.hasPrevious()
        )
    }

    private fun buildMetadata(request: MemorySearchRequest, searchTime: Long): SearchResultResponse.SearchMetadata {
        return SearchResultResponse.SearchMetadata(
            searchType = request.type,
            query = request.query,
            hashtags = request.hashtags,
            fromDate = request.fromDate,
            toDate = request.toDate,
            searchTimeMs = searchTime
        )
    }

    // ===== 자동완성 메서드들 =====

    /**
     * 게스트 사용자의 자동완성 (PUBLIC 메모리만) - 제목 + 해시태그
     */
    fun getPublicAutocomplete(query: String, limit: Int): AutocompleteResponse {
        val startTime = System.currentTimeMillis()

        validateAutocompleteQuery(query)

        val suggestions = mutableListOf<AutocompleteSuggestion>()

        // 항상 제목과 해시태그 모두 검색
        suggestions.addAll(memoryDocumentRepository.getPublicTitleSuggestions(query, limit / 2))
        suggestions.addAll(memoryDocumentRepository.getPublicHashtagSuggestions(query, limit / 2))

        // 점수 기준으로 정렬하고 제한
        val sortedSuggestions = suggestions.stream()
            .sorted { a, b -> java.lang.Float.compare(b.score, a.score) }
            .limit(limit.toLong())
            .toList()

        val responseTime = System.currentTimeMillis() - startTime

        return AutocompleteResponse(
            suggestions = sortedSuggestions,
            query = query,
            totalSuggestions = sortedSuggestions.size,
            responseTimeMs = responseTime
        )
    }

    /**
     * 인증된 사용자의 자동완성 (본인 + 관계된 사용자 + PUBLIC) - 제목 + 해시태그
     */
    fun getAuthenticatedAutocomplete(memberId: Long, query: String, limit: Int): AutocompleteResponse {
        val startTime = System.currentTimeMillis()

        validateAutocompleteQuery(query)

        val suggestions = mutableListOf<AutocompleteSuggestion>()

        // 항상 제목과 해시태그 모두 검색
        suggestions.addAll(memoryDocumentRepository.getAuthenticatedTitleSuggestions(memberId, query, limit / 2))
        suggestions.addAll(memoryDocumentRepository.getAuthenticatedHashtagSuggestions(memberId, query, limit / 2))

        // 점수 기준으로 정렬하고 제한
        val sortedSuggestions = suggestions.stream()
            .sorted { a, b -> java.lang.Float.compare(b.score, a.score) }
            .limit(limit.toLong())
            .toList()

        val responseTime = System.currentTimeMillis() - startTime

        return AutocompleteResponse(
            suggestions = sortedSuggestions,
            query = query,
            totalSuggestions = sortedSuggestions.size,
            responseTimeMs = responseTime
        )
    }

    private fun validateAutocompleteQuery(query: String) {
        if (!StringUtils.hasText(query)) {
            throw ValidationException("검색어는 필수입니다")
        }
        if (query.isEmpty()) {
            throw ValidationException("검색어는 최소 1자 이상이어야 합니다")
        }
        if (query.length > 100) {
            throw ValidationException("검색어는 최대 100자까지 입력 가능합니다")
        }
    }
}
