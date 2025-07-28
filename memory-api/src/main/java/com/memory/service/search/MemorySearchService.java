package com.memory.service.search;

import com.memory.document.memory.MemoryDocument;
import com.memory.document.memory.MemoryDocumentRepository;
import com.memory.dto.search.MemorySearchRequest;
import com.memory.dto.search.MemorySearchResponse;
import com.memory.dto.search.SearchResultResponse;
import com.memory.exception.customException.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MemorySearchService {

    private final MemoryDocumentRepository memoryDocumentRepository;

    /**
     * 게스트 사용자의 PUBLIC 메모리 검색
     */
    public SearchResultResponse searchPublic(MemorySearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        validateRequest(request);
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<SearchHit<MemoryDocument>> searchHits = executePublicSearch(request, pageable);
        
        List<MemorySearchResponse> memories = searchHits.getContent().stream()
                .map(hit -> convertToResponse(hit, request.isHighlight()))
                .toList();
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        return SearchResultResponse.builder()
                .memories(memories)
                .pageInfo(buildPageInfo(searchHits))
                .metadata(buildMetadata(request, searchTime))
                .build();
    }

    /**
     * 인증된 사용자의 메모리 검색 (본인 메모리 + 관계된 사용자 메모리 + PUBLIC 메모리)
     */
    public SearchResultResponse searchAuthenticated(MemorySearchRequest request, Long memberId) {
        long startTime = System.currentTimeMillis();
        
        validateRequest(request);
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        Page<SearchHit<MemoryDocument>> searchHits = executeAuthenticatedSearch(request, memberId, pageable);
        
        List<MemorySearchResponse> memories = searchHits.getContent().stream()
                .map(hit -> convertToResponse(hit, request.isHighlight()))
                .toList();
        
        long searchTime = System.currentTimeMillis() - startTime;
        
        return SearchResultResponse.builder()
                .memories(memories)
                .pageInfo(buildPageInfo(searchHits))
                .metadata(buildMetadata(request, searchTime))
                .build();
    }

    private void validateRequest(MemorySearchRequest request) {
        switch (request.getType()) {
            case ALL, TITLE, CONTENT, LOCATION -> {
                if (!StringUtils.hasText(request.getQuery())) {
                    throw new ValidationException("Query is required for " + request.getType() + " search");
                }
            }
            case HASHTAGS -> {
                if (request.getHashtags() == null || request.getHashtags().isEmpty()) {
                    throw new ValidationException("Hashtags are required for HASHTAGS search");
                }
            }
            case DATE -> {
                if (request.getFromDate() == null || request.getToDate() == null) {
                    throw new ValidationException("FromDate and ToDate are required for DATE search");
                }
                if (request.getFromDate().isAfter(request.getToDate())) {
                    throw new ValidationException("FromDate must be before or equal to ToDate");
                }
            }
        }
    }

    private Page<SearchHit<MemoryDocument>> executePublicSearch(MemorySearchRequest request, Pageable pageable) {
        return switch (request.getType()) {
            case ALL -> memoryDocumentRepository.searchPublicByAllFields(request.getQuery(), pageable);
            case TITLE -> memoryDocumentRepository.searchPublicByTitle(request.getQuery(), pageable);
            case CONTENT -> memoryDocumentRepository.searchPublicByContent(request.getQuery(), pageable);
            case HASHTAGS -> memoryDocumentRepository.searchPublicByHashtags(request.getHashtags(), pageable);
            case LOCATION -> memoryDocumentRepository.searchPublicByLocation(request.getQuery(), pageable);
            case DATE -> memoryDocumentRepository.searchPublicByDateRange(request.getFromDate(), request.getToDate(), pageable);
        };
    }

    private Page<SearchHit<MemoryDocument>> executeAuthenticatedSearch(MemorySearchRequest request, Long memberId, Pageable pageable) {
        return switch (request.getType()) {
            case ALL -> memoryDocumentRepository.searchByMemberAndAllFields(memberId, request.getQuery(), pageable);
            case TITLE -> memoryDocumentRepository.searchByMemberAndTitle(memberId, request.getQuery(), pageable);
            case CONTENT -> memoryDocumentRepository.searchByMemberAndContent(memberId, request.getQuery(), pageable);
            case HASHTAGS -> memoryDocumentRepository.searchByMemberAndHashtags(memberId, request.getHashtags(), pageable);
            case LOCATION -> memoryDocumentRepository.searchByMemberAndLocation(memberId, request.getQuery(), pageable);
            case DATE -> memoryDocumentRepository.searchByMemberAndDateRange(memberId, request.getFromDate(), request.getToDate(), pageable);
        };
    }

    private MemorySearchResponse convertToResponse(SearchHit<MemoryDocument> hit, boolean includeHighlight) {
        MemoryDocument document = hit.getContent();
        
        MemorySearchResponse.MemorySearchResponseBuilder builder = MemorySearchResponse.builder()
                .memoryId(document.getMemoryId())
                .title(document.getTitle())
                .content(document.getContent())
                .locationName(document.getLocationName())
                .memorableDate(document.getMemorableDate())
                .memorableDateText(document.getMemorableDateText())
                .memoryType(document.getMemoryType())
                .hashtags(document.getHashTags())
                .memberId(document.getMemberId())
                .relationshipMemberId(document.getRelationshipMemberId());

        if (includeHighlight && !hit.getHighlightFields().isEmpty()) {
            builder.highlights(buildHighlights(hit.getHighlightFields()));
        }

        return builder.build();
    }

    private MemorySearchResponse.HighlightInfo buildHighlights(Map<String, List<String>> highlightFields) {
        return MemorySearchResponse.HighlightInfo.builder()
                .title(highlightFields.get("title"))
                .content(highlightFields.get("content"))
                .locationName(highlightFields.get("locationName"))
                .hashtags(highlightFields.get("hashTags"))
                .build();
    }

    private SearchResultResponse.PageInfo buildPageInfo(Page<SearchHit<MemoryDocument>> searchHits) {
        return SearchResultResponse.PageInfo.builder()
                .currentPage(searchHits.getNumber())
                .totalPages(searchHits.getTotalPages())
                .pageSize(searchHits.getSize())
                .totalElements(searchHits.getTotalElements())
                .hasNext(searchHits.hasNext())
                .hasPrevious(searchHits.hasPrevious())
                .build();
    }

    private SearchResultResponse.SearchMetadata buildMetadata(MemorySearchRequest request, long searchTime) {
        return SearchResultResponse.SearchMetadata.builder()
                .searchType(request.getType())
                .query(request.getQuery())
                .hashtags(request.getHashtags())
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .searchTimeMs(searchTime)
                .build();
    }
}