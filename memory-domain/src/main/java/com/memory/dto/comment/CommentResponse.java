package com.memory.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.memory.domain.comment.Comment;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentResponse {

    private Long id;
    private String content;
    private Integer depth;
    private Long memoryId;
    private MemberInfo member;
    private Long parentCommentId;
    private List<CommentResponse> children;
    private Long childrenCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateDate;

    private Boolean isDeleted;
    private Boolean isAuthor;

    @Getter
    @Builder
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class MemberInfo {
        private Long id;
        private String nickname;
        private String profileImageUrl;
    }

    public static CommentResponse from(Comment comment) {
        return from(comment, null, false);
    }

    public static CommentResponse from(Comment comment, Long currentMemberId) {
        return from(comment, currentMemberId, false);
    }

    public static CommentResponse from(Comment comment, Long currentMemberId, boolean includeChildren) {
        CommentResponseBuilder builder = CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .depth(comment.getDepth())
                .memoryId(comment.getMemory().getId())
                .member(MemberInfo.builder()
                        .id(comment.getMember().getId())
                        .nickname(comment.getMember().getNickname())
                        .profileImageUrl(comment.getMember().getFile() != null ? 
                                comment.getMember().getFile().getFileUrl() : null)
                        .build())
                .parentCommentId(comment.getParent() != null ? comment.getParent().getId() : null)
                .childrenCount(comment.getActiveChildrenCount())
                .createDate(comment.getCreateDate())
                .updateDate(comment.getUpdateDate())
                .isDeleted(comment.isDeleted())
                .isAuthor(comment.getMember().getId().equals(currentMemberId));

        if (includeChildren && !comment.getChildren().isEmpty()) {
            List<CommentResponse> children = comment.getChildren().stream()
                    .filter(child -> !child.isDeleted())
                    .map(child -> CommentResponse.from(child, currentMemberId, false))
                    .collect(Collectors.toList());
            builder.children(children);
        }

        return builder.build();
    }

    public static List<CommentResponse> fromList(List<Comment> comments, Long currentMemberId) {
        return comments.stream()
                .map(comment -> CommentResponse.from(comment, currentMemberId, true))
                .collect(Collectors.toList());
    }
}
