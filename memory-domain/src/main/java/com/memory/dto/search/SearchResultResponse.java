package com.memory.dto.search;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class SearchResultResponse {

    private List<MemorySearchResponse> memories;
    private PageInfo pageInfo;
    private SearchMetadata metadata;

    @Getter
    @Builder
    public static class PageInfo {
        private int currentPage;
        private int totalPages;
        private int pageSize;
        private long totalElements;
        private boolean hasNext;
        private boolean hasPrevious;
    }

    @Getter
    @Builder
    public static class SearchMetadata {
        private SearchType searchType;
        private String query;
        private List<String> hashtags;
        private LocalDate fromDate;
        private LocalDate toDate;
        private long searchTimeMs;
    }
}