package com.memory.dto.comment;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentListResponse {

    private List<CommentResponse> comments;
    private Long totalCount;
    private Long topLevelCount;
    private Integer currentPage;
    private Integer pageSize;
    private Boolean hasNext;

    public static CommentListResponse of(List<CommentResponse> comments, Long totalCount, Long topLevelCount, 
                                       Integer currentPage, Integer pageSize, Boolean hasNext) {
        return CommentListResponse.builder()
                .comments(comments)
                .totalCount(totalCount)
                .topLevelCount(topLevelCount)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .hasNext(hasNext)
                .build();
    }
}
