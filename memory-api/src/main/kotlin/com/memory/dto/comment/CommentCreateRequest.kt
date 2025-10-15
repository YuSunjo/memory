package com.memory.dto.comment

import com.memory.domain.comment.Comment
import com.memory.domain.member.Member
import com.memory.domain.memory.Memory
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull


data class CommentCreateRequest(
    @field:NotNull(message = "메모리 ID는 필수입니다.")
    val memoryId: Long,

    @field:NotBlank(message = "댓글 내용은 필수입니다.")
    val content: String,

    val parentCommentId: Long? = null
) {
    fun toEntity(member: Member, memory: Memory, parentComment: Comment?): Comment {
        return Comment.create(content, memory, member, parentComment)
    }
}