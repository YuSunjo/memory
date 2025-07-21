package com.memory.dto.comment;

import com.memory.domain.comment.Comment;
import com.memory.dto.member.response.MemberResponse;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

    private Long id;
    private String content;
    private Integer depth;
    private Long memoryId;
    private MemberResponse member;
    private Long parentCommentId;
    private List<CommentResponse> children;
    private Long childrenCount;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;

    private Boolean isDeleted;
    private Boolean isAuthor;

    public static CommentResponse from(Comment comment) {
        return from(comment, null, false);
    }

    public static CommentResponse from(Comment comment, Long currentMemberId, boolean includeChildren) {
        MemberResponse memberResponse = MemberResponse.from(comment.getMember());
        Long parentCommentId = comment.getParent() != null ? comment.getParent().getId() : null;

        List<CommentResponse> childrenResponses = includeChildren
                ? comment.getChildren().stream()
                        .map(child -> CommentResponse.from(child, currentMemberId, true))
                        .collect(Collectors.toList())
                : null;

        return new CommentResponse(
                comment.getId(),
                comment.getContent(),
                comment.getDepth(),
                comment.getMemory().getId(),
                memberResponse,
                parentCommentId,
                childrenResponses,
                (long) (childrenResponses != null ? childrenResponses.size() : 0),
                comment.getCreateDate(),
                comment.getUpdateDate(),
                comment.isDeleted(),
                comment.getMember().getId().equals(currentMemberId)
        );
    }

    public static List<CommentResponse> fromList(List<Comment> comments, Long currentMemberId) {
        return comments.stream()
                .map(comment -> CommentResponse.from(comment, currentMemberId, true))
                .collect(Collectors.toList());
    }
}
