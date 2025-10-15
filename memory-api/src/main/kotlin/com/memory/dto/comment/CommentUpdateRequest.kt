package com.memory.dto.comment

import jakarta.validation.constraints.NotBlank

data class CommentUpdateRequest private constructor(
    @field:NotBlank(message = "댓글 내용은 필수입니다.")
    val content: String,
) {
    companion object {
        @JvmStatic
        fun of(content: String): CommentUpdateRequest =
            CommentUpdateRequest(content = content)

        @JvmStatic
        fun testInstance(content: String): CommentUpdateRequest =
            CommentUpdateRequest(content = content)
    }
}