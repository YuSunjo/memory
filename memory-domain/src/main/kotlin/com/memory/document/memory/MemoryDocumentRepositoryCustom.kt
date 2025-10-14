package com.memory.document.memory

import com.memory.dto.search.AutocompleteSuggestion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.elasticsearch.core.SearchHit
import java.time.LocalDate

interface MemoryDocumentRepositoryCustom {

    // ===== PUBLIC 메모리 전용 검색 메서드들 =====

    /**
     * PUBLIC 메모리 전체 검색 (제목, 내용, 해시태그)
     */
    fun searchPublicByAllFields(query: String?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    /**
     * PUBLIC 메모리 제목 검색
     */
    fun searchPublicByTitle(query: String?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    /**
     * PUBLIC 메모리 내용 검색
     */
    fun searchPublicByContent(query: String?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    /**
     * PUBLIC 메모리 해시태그 검색
     */
    fun searchPublicByHashtags(hashtags: List<String>?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    /**
     * PUBLIC 메모리 위치명 검색
     */
    fun searchPublicByLocation(query: String?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    /**
     * PUBLIC 메모리 날짜 범위 검색
     */
    fun searchPublicByDateRange(fromDate: LocalDate?, toDate: LocalDate?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    // ===== 인증된 사용자 검색 메서드들 (본인 메모리 + PUBLIC) =====

    /**
     * 사용자별 전체 검색 (본인 메모리 + PUBLIC 메모리)
     */
    fun searchByMemberAndAllFields(memberId: Long?, query: String?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    /**
     * 사용자별 제목 검색
     */
    fun searchByMemberAndTitle(memberId: Long?, query: String?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    /**
     * 사용자별 내용 검색
     */
    fun searchByMemberAndContent(memberId: Long?, query: String?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    /**
     * 사용자별 해시태그 검색
     */
    fun searchByMemberAndHashtags(memberId: Long?, hashtags: List<String>?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    /**
     * 사용자별 위치명 검색
     */
    fun searchByMemberAndLocation(memberId: Long?, query: String?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    /**
     * 사용자별 날짜 범위 검색
     */
    fun searchByMemberAndDateRange(memberId: Long?, fromDate: LocalDate?, toDate: LocalDate?, pageable: Pageable?): Page<SearchHit<MemoryDocument>>

    // ===== 자동완성 메서드들 =====

    /**
     * PUBLIC 메모리 제목 자동완성
     */
    fun getPublicTitleSuggestions(query: String?, limit: Int): List<AutocompleteSuggestion>

    /**
     * PUBLIC 메모리 해시태그 자동완성
     */
    fun getPublicHashtagSuggestions(query: String?, limit: Int): List<AutocompleteSuggestion>

    /**
     * 인증된 사용자 제목 자동완성 (본인 + 관계된 사용자 + PUBLIC)
     */
    fun getAuthenticatedTitleSuggestions(memberId: Long?, query: String?, limit: Int): List<AutocompleteSuggestion>

    /**
     * 인증된 사용자 해시태그 자동완성 (본인 + 관계된 사용자 + PUBLIC)
     */
    fun getAuthenticatedHashtagSuggestions(memberId: Long?, query: String?, limit: Int): List<AutocompleteSuggestion>
}
