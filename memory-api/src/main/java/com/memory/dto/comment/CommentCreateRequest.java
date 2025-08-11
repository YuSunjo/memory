package com.memory.dto.comment;

import com.memory.domain.comment.Comment;
import com.memory.domain.member.Member;
import com.memory.domain.memory.Memory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@AllArgsConstructor
public class CommentCreateRequest {

    @NotNull(message = "메모리 ID는 필수입니다.")
    private Long memoryId;

    @NotBlank(message = "댓글 내용은 필수입니다.")
    private String content;

    private Long parentCommentId;

    public Comment toEntity(Member member, Memory memory, Comment parentComment) {
        return Comment.create(content, memory, member, parentComment);
    }
}
