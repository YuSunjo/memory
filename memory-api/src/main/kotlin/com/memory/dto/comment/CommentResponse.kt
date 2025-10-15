package com.memory.dto.comment

import com.memory.domain.comment.Comment
import com.memory.dto.member.response.MemberResponse
import java.time.LocalDateTime

data class CommentResponse private constructor(
    val id: Long?,
    val content: String,
    val depth: Int,
    val memoryId: Long?,
    val member: MemberResponse,
    val parentCommentId: Long?,
    val children: List<CommentResponse>?,
    val childrenCount: Long,
    val createDate: LocalDateTime?,
    val updateDate: LocalDateTime?,
    val isDeleted: Boolean,
    val isAuthor: Boolean
) {
    companion object {
        @JvmStatic
        fun from(comment: Comment): CommentResponse =
            from(comment, null, false)

        @JvmStatic
        fun from(
            comment: Comment,
            currentMemberId: Long?,
            includeChildren: Boolean
        ): CommentResponse {
            val memberResponse = MemberResponse.from(comment.member)
            val parentId = comment.parent?.id

            val childrenResponses: List<CommentResponse>? =
                if (includeChildren)
                    comment.children.map { child -> from(child, currentMemberId, true) }
                else null

            val count = childrenResponses?.size?.toLong() ?: 0L
            val author = currentMemberId != null && comment.member.id == currentMemberId

            return CommentResponse(
                id = comment.id,
                content = comment.content,
                depth = comment.depth,
                memoryId = comment.memory.id,
                member = memberResponse,
                parentCommentId = parentId,
                children = childrenResponses,
                childrenCount = count,
                createDate = comment.createDate,
                updateDate = comment.updateDate,
                isDeleted = comment.isDeleted(),
                isAuthor = author
            )
        }

        @JvmStatic
        fun fromList(comments: List<Comment>, currentMemberId: Long?): List<CommentResponse> =
            comments.map { from(it, currentMemberId, true) }
    }
}
