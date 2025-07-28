package com.memory.document.memory;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.SearchHit;

import java.time.LocalDate;
import java.util.List;

public interface MemoryDocumentRepositoryCustom {

    // ===== PUBLIC 메모리 전용 검색 메서드들 =====

    /**
     * PUBLIC 메모리 전체 검색 (제목, 내용, 해시태그)
     */
    Page<SearchHit<MemoryDocument>> searchPublicByAllFields(String query, Pageable pageable);

    /**
     * PUBLIC 메모리 제목 검색
     */
    Page<SearchHit<MemoryDocument>> searchPublicByTitle(String query, Pageable pageable);

    /**
     * PUBLIC 메모리 내용 검색
     */
    Page<SearchHit<MemoryDocument>> searchPublicByContent(String query, Pageable pageable);

    /**
     * PUBLIC 메모리 해시태그 검색
     */
    Page<SearchHit<MemoryDocument>> searchPublicByHashtags(List<String> hashtags, Pageable pageable);

    /**
     * PUBLIC 메모리 위치명 검색
     */
    Page<SearchHit<MemoryDocument>> searchPublicByLocation(String query, Pageable pageable);

    /**
     * PUBLIC 메모리 날짜 범위 검색
     */
    Page<SearchHit<MemoryDocument>> searchPublicByDateRange(LocalDate fromDate, LocalDate toDate, Pageable pageable);

    // ===== 인증된 사용자 검색 메서드들 (본인 메모리 + PUBLIC) =====

    /**
     * 사용자별 전체 검색 (본인 메모리 + PUBLIC 메모리)
     */
    Page<SearchHit<MemoryDocument>> searchByMemberAndAllFields(Long memberId, String query, Pageable pageable);

    /**
     * 사용자별 제목 검색
     */
    Page<SearchHit<MemoryDocument>> searchByMemberAndTitle(Long memberId, String query, Pageable pageable);

    /**
     * 사용자별 내용 검색
     */
    Page<SearchHit<MemoryDocument>> searchByMemberAndContent(Long memberId, String query, Pageable pageable);

    /**
     * 사용자별 해시태그 검색
     */
    Page<SearchHit<MemoryDocument>> searchByMemberAndHashtags(Long memberId, List<String> hashtags, Pageable pageable);

    /**
     * 사용자별 위치명 검색
     */
    Page<SearchHit<MemoryDocument>> searchByMemberAndLocation(Long memberId, String query, Pageable pageable);

    /**
     * 사용자별 날짜 범위 검색
     */
    Page<SearchHit<MemoryDocument>> searchByMemberAndDateRange(Long memberId, LocalDate fromDate, LocalDate toDate, Pageable pageable);
}