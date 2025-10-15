package com.memory.dto.comment

data class CommentListResponse(
    val comments: List<CommentResponse>,
    val totalCount: Long,
    val topLevelCount: Long,
    val currentPage: Int,
    val pageSize: Int,
    val hasNext: Boolean,

) {
    companion object {
        @JvmStatic
        fun of(
            comments: List<CommentResponse>,
            totalCount: Long,
            topLevelCount: Long,
            currentPage: Int,
            pageSize: Int,
            hasNext: Boolean
        ): CommentListResponse =
            CommentListResponse(
                comments = comments,
                totalCount = totalCount,
                topLevelCount = topLevelCount,
                currentPage = currentPage,
                pageSize = pageSize,
                hasNext = hasNext
            )
    }
}