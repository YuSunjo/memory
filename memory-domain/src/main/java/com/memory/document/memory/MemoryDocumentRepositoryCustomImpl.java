package com.memory.document.memory;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.TermsQuery;
import com.memory.dto.search.AutocompleteSuggestion;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemoryDocumentRepositoryCustomImpl implements MemoryDocumentRepositoryCustom {

    private final ElasticsearchOperations elasticsearchOperations;
    private static final String INDEX_NAME = "memory";

    // ===== PUBLIC 메모리 전용 검색 메서드들 =====

    @Override
    public Page<SearchHit<MemoryDocument>> searchPublicByAllFields(String query, Pageable pageable) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(createPublicMemoryFilter())
                .must(MultiMatchQuery.of(m -> m
                        .query(query)
                        .fields("title", "content", "hashTags", "locationName", "memorableDateText")
                )._toQuery())
        )._toQuery();

        return executeSearchWithHighlight(boolQuery, pageable, "title", "content", "hashTags");
    }

    @Override
    public Page<SearchHit<MemoryDocument>> searchPublicByTitle(String query, Pageable pageable) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(createPublicMemoryFilter())
                .must(Query.of(q -> q
                        .match(match -> match
                                .field("title")
                                .query(query)
                        )
                ))
        )._toQuery();

        return executeSearchWithHighlight(boolQuery, pageable, "title");
    }

    @Override
    public Page<SearchHit<MemoryDocument>> searchPublicByContent(String query, Pageable pageable) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(createPublicMemoryFilter())
                .must(Query.of(q -> q
                        .match(match -> match
                                .field("content")
                                .query(query)
                        )
                ))
        )._toQuery();

        return executeSearchWithHighlight(boolQuery, pageable, "content");
    }

    @Override
    public Page<SearchHit<MemoryDocument>> searchPublicByHashtags(List<String> hashtags, Pageable pageable) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(createPublicMemoryFilter())
                .must(TermsQuery.of(t -> t
                        .field("hashTags")
                        .terms(terms -> terms.value(hashtags.stream()
                                .map(FieldValue::of)
                                .toList()))
                )._toQuery())
        )._toQuery();

        return executeSearchWithHighlight(boolQuery, pageable, "hashTags");
    }

    @Override
    public Page<SearchHit<MemoryDocument>> searchPublicByLocation(String query, Pageable pageable) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(createPublicMemoryFilter())
                .must(Query.of(q -> q
                        .match(match -> match
                                .field("locationName")
                                .query(query)
                        )
                ))
        )._toQuery();

        return executeSearchWithHighlight(boolQuery, pageable, "locationName");
    }

    @Override
    public Page<SearchHit<MemoryDocument>> searchPublicByDateRange(LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        // TODO: Range query 구현 필요
        Query boolQuery = createPublicMemoryFilter();
        return executeSearch(boolQuery, pageable);
    }

    // ===== 인증된 사용자 검색 메서드들 (본인 메모리 + PUBLIC) =====

    @Override
    public Page<SearchHit<MemoryDocument>> searchByMemberAndAllFields(Long memberId, String query, Pageable pageable) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(MultiMatchQuery.of(m -> m
                        .query(query)
                        .fields("title", "content", "hashTags", "locationName", "memorableDateText")
                )._toQuery())
                .must(createMemberOrPublicFilter(memberId))
        )._toQuery();

        return executeSearchWithHighlight(boolQuery, pageable, "title", "content", "hashTags");
    }

    @Override
    public Page<SearchHit<MemoryDocument>> searchByMemberAndTitle(Long memberId, String query, Pageable pageable) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(Query.of(q -> q
                        .match(match -> match
                                .field("title")
                                .query(query)
                        )
                ))
                .must(createMemberOrPublicFilter(memberId))
        )._toQuery();

        return executeSearchWithHighlight(boolQuery, pageable, "title");
    }

    @Override
    public Page<SearchHit<MemoryDocument>> searchByMemberAndContent(Long memberId, String query, Pageable pageable) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(Query.of(q -> q
                        .match(match -> match
                                .field("content")
                                .query(query)
                        )
                ))
                .must(createMemberOrPublicFilter(memberId))
        )._toQuery();

        return executeSearchWithHighlight(boolQuery, pageable, "content");
    }

    @Override
    public Page<SearchHit<MemoryDocument>> searchByMemberAndHashtags(Long memberId, List<String> hashtags, Pageable pageable) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(TermsQuery.of(t -> t
                        .field("hashTags")
                        .terms(terms -> terms.value(hashtags.stream()
                                .map(FieldValue::of)
                                .toList()))
                )._toQuery())
                .must(createMemberOrPublicFilter(memberId))
        )._toQuery();

        return executeSearchWithHighlight(boolQuery, pageable, "hashTags");
    }

    @Override
    public Page<SearchHit<MemoryDocument>> searchByMemberAndLocation(Long memberId, String query, Pageable pageable) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(Query.of(q -> q
                        .match(match -> match
                                .field("locationName")
                                .query(query)
                        )
                ))
                .must(createMemberOrPublicFilter(memberId))
        )._toQuery();

        return executeSearchWithHighlight(boolQuery, pageable, "locationName");
    }

    @Override
    public Page<SearchHit<MemoryDocument>> searchByMemberAndDateRange(Long memberId, LocalDate fromDate, LocalDate toDate, Pageable pageable) {
        // TODO: Range query 구현 필요
        Query boolQuery = createMemberOrPublicFilter(memberId);
        return executeSearch(boolQuery, pageable);
    }

    // ===== 헬퍼 메서드들 =====

    /**
     * PUBLIC 메모리만 필터링하는 쿼리 생성
     */
    private Query createPublicMemoryFilter() {
        return TermQuery.of(t -> t
                .field("memoryType")
                .value("PUBLIC")
        )._toQuery();
    }

    /**
     * 사용자의 메모리, 관계된 사용자의 메모리 또는 PUBLIC 메모리를 필터링하는 쿼리 생성
     */
    private Query createMemberOrPublicFilter(Long memberId) {
        return BoolQuery.of(b -> b
                .should(TermQuery.of(t -> t
                        .field("memberId")
                        .value(memberId)
                )._toQuery())
                .should(TermsQuery.of(t -> t
                        .field("relationshipMemberId")
                        .terms(terms -> terms.value(List.of(FieldValue.of(memberId))))
                )._toQuery())
                .should(TermQuery.of(t -> t
                        .field("memoryType")
                        .value("PUBLIC")
                )._toQuery())
                .minimumShouldMatch("1")
        )._toQuery();
    }

    /**
     * 하이라이팅 없이 검색 실행
     */
    private Page<SearchHit<MemoryDocument>> executeSearch(Query query, Pageable pageable) {
        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .build();

        SearchHits<MemoryDocument> searchHits = elasticsearchOperations.search(
                searchQuery, MemoryDocument.class, IndexCoordinates.of(INDEX_NAME));

        return PageableExecutionUtils.getPage(
                searchHits.getSearchHits(),
                pageable,
                searchHits::getTotalHits
        );
    }

    /**
     * 하이라이팅과 함께 검색 실행 (하이라이팅은 추후 구현)
     */
    private Page<SearchHit<MemoryDocument>> executeSearchWithHighlight(Query query, Pageable pageable, String... fields) {
        return executeSearch(query, pageable);
    }

    // ===== 자동완성 메서드 구현 =====

    @Override
    public List<AutocompleteSuggestion> getPublicTitleSuggestions(String query, int limit) {
        return getTitleSuggestions(createPublicMemoryFilter(), query, limit);
    }

    @Override
    public List<AutocompleteSuggestion> getPublicHashtagSuggestions(String query, int limit) {
        return getHashtagSuggestions(createPublicMemoryFilter(), query, limit);
    }

    @Override
    public List<AutocompleteSuggestion> getAuthenticatedTitleSuggestions(Long memberId, String query, int limit) {
        return getTitleSuggestions(createMemberOrPublicFilter(memberId), query, limit);
    }

    @Override
    public List<AutocompleteSuggestion> getAuthenticatedHashtagSuggestions(Long memberId, String query, int limit) {
        return getHashtagSuggestions(createMemberOrPublicFilter(memberId), query, limit);
    }

    /**
     * 제목 자동완성 검색 실행 (Prefix Query 사용)
     */
    private List<AutocompleteSuggestion> getTitleSuggestions(Query filter, String query, int limit) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(filter)
                .must(Query.of(q -> q
                        .prefix(p -> p
                                .field("title")
                                .value(query.toLowerCase())
                        )
                ))
        )._toQuery();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withMaxResults(limit * 2) // 중복 제거를 위해 더 많이 가져옴
                .withSourceFilter(new FetchSourceFilter(
                        true, new String[]{"title"}, null))
                .build();

        SearchHits<MemoryDocument> searchHits = elasticsearchOperations.search(
                searchQuery, MemoryDocument.class, IndexCoordinates.of(INDEX_NAME));

        return searchHits.getSearchHits().stream()
                .map(hit -> AutocompleteSuggestion.builder()
                        .text(hit.getContent().getTitle())
                        .type(AutocompleteSuggestion.SuggestionType.TITLE)
                        .score(hit.getScore())
                        .matchCount(1L)
                        .build())
                .distinct() // 중복 제거
                .limit(limit)
                .toList();
    }

    /**
     * 해시태그 자동완성 검색 실행 (Wildcard + Aggregation 사용)
     */
    private List<AutocompleteSuggestion> getHashtagSuggestions(Query filter, String query, int limit) {
        Query boolQuery = BoolQuery.of(b -> b
                .must(filter)
                .must(Query.of(q -> q
                        .wildcard(w -> w
                                .field("hashTags")
                                .value(query.toLowerCase() + "*")
                        )
                ))
        )._toQuery();

        NativeQuery searchQuery = NativeQuery.builder()
                .withQuery(boolQuery)
                .withMaxResults(limit)
                .withSourceFilter(new FetchSourceFilter(
                        true, new String[]{"hashTags"}, null))
                .build();

        SearchHits<MemoryDocument> searchHits = elasticsearchOperations.search(
                searchQuery, MemoryDocument.class, IndexCoordinates.of(INDEX_NAME));

        // 검색 결과에서 해시태그들을 추출하고 중복 제거
        return searchHits.getSearchHits().stream()
                .flatMap(hit -> hit.getContent().getHashTags().stream())
                .filter(hashtag -> hashtag.toLowerCase().startsWith(query.toLowerCase()))
                .distinct()
                .map(hashtag -> AutocompleteSuggestion.builder()
                        .text(hashtag)
                        .type(AutocompleteSuggestion.SuggestionType.HASHTAG)
                        .matchCount(1L)
                        .score(1.0f)
                        .build())
                .limit(limit)
                .toList();
    }
}