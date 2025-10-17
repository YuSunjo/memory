package com.memory.controller.comment

import com.memory.annotation.Auth
import com.memory.annotation.MemberId
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.dto.comment.CommentCreateRequest
import com.memory.dto.comment.CommentListResponse
import com.memory.dto.comment.CommentResponse
import com.memory.dto.comment.CommentUpdateRequest
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.comment.CommentService
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.*

@RestController
@Tag(name = "Comment", description = "Comment API")
class CommentController(
    private val commentService: CommentService,
) {
    @SecuredApi(
        summary = "댓글 생성",
        description = "새로운 댓글을 생성합니다. parentCommentId가 있으면 대댓글로 생성됩니다.",
        response = CommentResponse::class
    )
    @Auth
    @PostMapping("api/v1/comments")
    fun createComment(
        @Parameter(hidden = true) @MemberId memberId: Long?,
        @RequestBody request: @Valid CommentCreateRequest
    ): ServerResponse<CommentResponse?> {
        return success(commentService.createComment(request, memberId))
    }

    @SecuredApi(
        summary = "메모리의 최상위 댓글 목록 조회 (페이징)",
        description = "메모리의 최상위 댓글만 페이징으로 조회합니다.",
        response = CommentListResponse::class
    )
    @Auth
    @GetMapping("api/v1/comments/memory/{memoryId}/top-level")
    fun getTopLevelCommentsByMemory(
        @PathVariable memoryId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<CommentListResponse?> {
        return success(
            commentService.getTopLevelCommentsByMemory(
                memoryId,
                page,
                size,
                memberId
            )
        )
    }

    @SecuredApi(
        summary = "로그인 안되어 있을 경우 메모리의 최상위 댓글 목록 조회 (페이징)",
        description = "메모리의 최상위 댓글만 페이징으로 조회합니다.",
        response = CommentListResponse::class
    )
    @GetMapping("api/v1/comments/memory/public/{memoryId}/top-level")
    fun getTopLevelCommentsByPublicMemory(
        @PathVariable memoryId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): ServerResponse<CommentListResponse?> {
        return success(commentService.getTopLevelCommentsByPublicMemory(memoryId, page, size))
    }

    @SecuredApi(
        summary = "특정 댓글의 대댓글 목록 조회",
        description = "특정 댓글에 달린 대댓글 목록을 조회합니다.",
        response = CommentListResponse::class
    )
    @Auth
    @GetMapping("api/v1/comments/{commentId}/replies")
    fun getRepliesByComment(
        @PathVariable commentId: Long?,
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<CommentListResponse?> {
        return success(commentService.getRepliesByComment(commentId, memberId))
    }

    @SecuredApi(summary = "댓글 상세 조회", description = "댓글 ID로 댓글을 조회합니다.", response = CommentResponse::class)
    @Auth
    @GetMapping("api/v1/comments/{commentId}")
    fun getComment(
        @PathVariable commentId: Long?,
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<CommentResponse?> {
        return success(commentService.getComment(commentId, memberId))
    }

    @SecuredApi(
        summary = "댓글 수정",
        description = "기존 댓글의 내용을 수정합니다. 작성자만 수정할 수 있습니다.",
        response = CommentResponse::class
    )
    @Auth
    @PutMapping("api/v1/comments/{commentId}")
    fun updateComment(
        @PathVariable commentId: Long?,
        @RequestBody request: @Valid CommentUpdateRequest,
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<CommentResponse?> {
        return success(commentService.updateComment(commentId, request, memberId))
    }

    @SecuredApi(summary = "댓글 삭제", description = "댓글을 삭제합니다. 작성자만 삭제할 수 있습니다. 대댓글이 있는 경우 내용만 변경되고, 없는 경우 완전 삭제됩니다.")
    @Auth
    @DeleteMapping("api/v1/comments/{commentId}")
    fun deleteComment(
        @PathVariable commentId: Long?,
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<String> {
        commentService.deleteComment(commentId, memberId)
        return ServerResponse.OK
    }

    @SecuredApi(
        summary = "멤버의 댓글 목록 조회",
        description = "특정 멤버가 작성한 댓글 목록을 조회합니다.",
        response = CommentListResponse::class
    )
    @Auth
    @GetMapping("api/v1/comments/member/{memberId}")
    fun getCommentsByMember(
        @PathVariable memberId: Long?,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int,
        @Parameter(hidden = true) @MemberId currentMemberId: Long?
    ): ServerResponse<CommentListResponse?> {
        return success(
            commentService.getCommentsByMember(
                memberId,
                page,
                size,
                currentMemberId
            )
        )
    }

    @SecuredApi(summary = "최근 댓글 조회", description = "전체 메모리의 최근 댓글을 조회합니다.", response = CommentListResponse::class)
    @Auth
    @GetMapping("api/v1/comments/recent")
    fun getRecentComments(
        @RequestParam(defaultValue = "10") limit: Int,
        @Parameter(hidden = true) @MemberId memberId: Long?
    ): ServerResponse<CommentListResponse?> {
        return success(commentService.getRecentComments(limit, memberId))
    }
}
