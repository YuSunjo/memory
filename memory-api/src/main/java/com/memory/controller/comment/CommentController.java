package com.memory.controller.comment;

import com.memory.annotation.Auth;
import com.memory.annotation.MemberId;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.dto.comment.CommentCreateRequest;
import com.memory.dto.comment.CommentListResponse;
import com.memory.dto.comment.CommentResponse;
import com.memory.dto.comment.CommentUpdateRequest;
import com.memory.response.ServerResponse;
import com.memory.service.comment.CommentService;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "Comment", description = "Comment API")
public class CommentController {

    private final CommentService commentService;

    @ApiOperations.SecuredApi(
        summary = "댓글 생성",
        description = "새로운 댓글을 생성합니다. parentCommentId가 있으면 대댓글로 생성됩니다.",
        response = CommentResponse.class
    )
    @Auth
    @PostMapping("api/v1/comments")
    public ServerResponse<CommentResponse> createComment(
            @Parameter(hidden = true) @MemberId Long memberId,
            @RequestBody @Valid CommentCreateRequest request) {
        return ServerResponse.success(commentService.createComment(request, memberId));
    }

    @ApiOperations.SecuredApi(
        summary = "메모리의 최상위 댓글 목록 조회 (페이징)",
        description = "메모리의 최상위 댓글만 페이징으로 조회합니다.",
        response = CommentListResponse.class
    )
    @Auth
    @GetMapping("api/v1/comments/memory/{memoryId}/top-level")
    public ServerResponse<CommentListResponse> getTopLevelCommentsByMemory(
            @PathVariable Long memoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @MemberId Long memberId) {
        return ServerResponse.success(commentService.getTopLevelCommentsByMemory(memoryId, page, size, memberId));
    }

    @ApiOperations.SecuredApi(
            summary = "로그인 안되어 있을 경우 메모리의 최상위 댓글 목록 조회 (페이징)",
            description = "메모리의 최상위 댓글만 페이징으로 조회합니다.",
            response = CommentListResponse.class
    )
    @GetMapping("api/v1/comments/memory/public/{memoryId}/top-level")
    public ServerResponse<CommentListResponse> getTopLevelCommentsByPublicMemory(
            @PathVariable Long memoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ServerResponse.success(commentService.getTopLevelCommentsByPublicMemory(memoryId, page, size));
    }

    @ApiOperations.SecuredApi(
        summary = "특정 댓글의 대댓글 목록 조회",
        description = "특정 댓글에 달린 대댓글 목록을 조회합니다.",
        response = CommentListResponse.class
    )
    @Auth
    @GetMapping("api/v1/comments/{commentId}/replies")
    public ServerResponse<CommentListResponse> getRepliesByComment(
            @PathVariable Long commentId,
            @Parameter(hidden = true) @MemberId Long memberId) {
        return ServerResponse.success(commentService.getRepliesByComment(commentId, memberId));
    }

    @ApiOperations.SecuredApi(
        summary = "댓글 상세 조회",
        description = "댓글 ID로 댓글을 조회합니다.",
        response = CommentResponse.class
    )
    @Auth
    @GetMapping("api/v1/comments/{commentId}")
    public ServerResponse<CommentResponse> getComment(
            @PathVariable Long commentId,
            @Parameter(hidden = true) @MemberId Long memberId) {
        return ServerResponse.success(commentService.getComment(commentId, memberId));
    }

    @ApiOperations.SecuredApi(
        summary = "댓글 수정",
        description = "기존 댓글의 내용을 수정합니다. 작성자만 수정할 수 있습니다.",
        response = CommentResponse.class
    )
    @Auth
    @PutMapping("api/v1/comments/{commentId}")
    public ServerResponse<CommentResponse> updateComment(
            @PathVariable Long commentId,
            @RequestBody @Valid CommentUpdateRequest request,
            @Parameter(hidden = true) @MemberId Long memberId) {
        return ServerResponse.success(commentService.updateComment(commentId, request, memberId));
    }

    @ApiOperations.SecuredApi(
        summary = "댓글 삭제",
        description = "댓글을 삭제합니다. 작성자만 삭제할 수 있습니다. 대댓글이 있는 경우 내용만 변경되고, 없는 경우 완전 삭제됩니다."
    )
    @Auth
    @DeleteMapping("api/v1/comments/{commentId}")
    public ServerResponse<String> deleteComment(
            @PathVariable Long commentId,
            @Parameter(hidden = true) @MemberId Long memberId) {
        commentService.deleteComment(commentId, memberId);
        return ServerResponse.OK;
    }

    @ApiOperations.SecuredApi(
        summary = "멤버의 댓글 목록 조회",
        description = "특정 멤버가 작성한 댓글 목록을 조회합니다.",
        response = CommentListResponse.class
    )
    @Auth
    @GetMapping("api/v1/comments/member/{memberId}")
    public ServerResponse<CommentListResponse> getCommentsByMember(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @Parameter(hidden = true) @MemberId Long currentMemberId) {
        return ServerResponse.success(commentService.getCommentsByMember(memberId, page, size, currentMemberId));
    }

    @ApiOperations.SecuredApi(
        summary = "최근 댓글 조회",
        description = "전체 메모리의 최근 댓글을 조회합니다.",
        response = CommentListResponse.class
    )
    @Auth
    @GetMapping("api/v1/comments/recent")
    public ServerResponse<CommentListResponse> getRecentComments(
            @RequestParam(defaultValue = "10") int limit,
            @Parameter(hidden = true) @MemberId Long memberId) {
        return ServerResponse.success(commentService.getRecentComments(limit, memberId));
    }
}
