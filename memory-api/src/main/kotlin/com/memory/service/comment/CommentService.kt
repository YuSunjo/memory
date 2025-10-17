package com.memory.service.comment

import com.memory.domain.comment.Comment
import com.memory.domain.comment.repository.CommentRepository
import com.memory.domain.member.Member
import com.memory.domain.member.repository.MemberRepository
import com.memory.domain.memory.Memory
import com.memory.domain.memory.repository.MemoryRepository
import com.memory.dto.comment.CommentCreateRequest
import com.memory.dto.comment.CommentListResponse
import com.memory.dto.comment.CommentResponse
import com.memory.dto.comment.CommentUpdateRequest
import com.memory.exception.customException.NotFoundException
import com.memory.exception.customException.ValidationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Supplier

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val memberRepository: MemberRepository,
    private val memoryRepository: MemoryRepository,
) {
    @Transactional
    fun createComment(request: CommentCreateRequest, memberId: Long?): CommentResponse {
        val member = findMemberById(memberId)
        val memory = findMemoryById(request.memoryId)

        var parentComment: Comment? = null
        if (request.parentCommentId != null) {
            parentComment = findCommentById(request.parentCommentId)


            // 대댓글을 달 수 있는지 확인
            if (!parentComment.canHaveReply()) {
                throw ValidationException("해당 댓글에는 더 이상 답글을 달 수 없습니다.")
            }


            // 부모 댓글이 같은 메모리에 속하는지 확인
            if (parentComment.memory.id != memory.id) {
                throw ValidationException("댓글과 대댓글은 같은 메모리에 속해야 합니다.")
            }


            // 부모 댓글이 삭제되었는지 확인
            if (parentComment.isDeleted()) {
                throw ValidationException("삭제된 댓글에는 답글을 달 수 없습니다.")
            }
        }

        val comment = request.toEntity(member, memory, parentComment)
        val savedComment = commentRepository.save<Comment>(comment)
        memory.addComment(savedComment)

        return CommentResponse.from(savedComment, memberId, false)
    }

    @Transactional(readOnly = true)
    fun getTopLevelCommentsByMemory(memoryId: Long?, page: Int, size: Int, memberId: Long?): CommentListResponse {
        findMemberById(memberId)
        val memory = findMemoryById(memoryId)

        val comments: List<Comment> = commentRepository.findTopLevelCommentsByMemory(memory, page, size)
        val commentResponses: List<CommentResponse> = CommentResponse.fromList(comments, memberId)

        val totalCount = commentRepository.countActiveCommentsByMemory(memory)
        val topLevelCount = memory.getTopLevelCommentsCount()
        val hasNext = comments.size == size

        return CommentListResponse.of(commentResponses, totalCount, topLevelCount, page, size, hasNext)
    }

    @Transactional(readOnly = true)
    fun getTopLevelCommentsByPublicMemory(memoryId: Long?, page: Int, size: Int): CommentListResponse {
        val memory = findMemoryById(memoryId)
        val comments: List<Comment> = commentRepository.findTopLevelCommentsByMemory(memory, page, size)
        val commentResponses: List<CommentResponse> = CommentResponse.fromList(comments, null)

        val totalCount = commentRepository.countActiveCommentsByMemory(memory)
        val topLevelCount = memory.getTopLevelCommentsCount()
        val hasNext = comments.size == size

        return CommentListResponse.of(commentResponses, totalCount, topLevelCount, page, size, hasNext)
    }

    @Transactional(readOnly = true)
    fun getRepliesByComment(commentId: Long?, memberId: Long?): CommentListResponse {
        findMemberById(memberId)
        val parentComment = findCommentById(commentId)

        val replies: List<Comment> = commentRepository.findRepliesByParentComment(parentComment)
        val replyResponses: List<CommentResponse> = CommentResponse.fromList(replies, memberId)

        return CommentListResponse.of(replyResponses, replies.size.toLong(), 0L, 0, replies.size, false)
    }

    @Transactional(readOnly = true)
    fun getComment(commentId: Long?, memberId: Long?): CommentResponse {
        findMemberById(memberId)
        val comment = commentRepository.findByIdWithMember(commentId)
            .orElseThrow<NotFoundException?>(Supplier { NotFoundException("댓글을 찾을 수 없습니다.") })

        return CommentResponse.from(comment, memberId, true)
    }

    @Transactional
    fun updateComment(commentId: Long?, request: CommentUpdateRequest, memberId: Long?): CommentResponse {
        val member = findMemberById(memberId)
        val comment = findCommentById(commentId)

        if (!comment.isAuthor(member)) {
            throw ValidationException("댓글을 수정할 권한이 없습니다.")
        }


        // 삭제된 댓글인지 확인
        if (comment.isDeleted()) {
            throw ValidationException("삭제된 댓글은 수정할 수 없습니다.")
        }

        comment.updateContent(request.content)

        return CommentResponse.from(comment, memberId, !comment.children.isEmpty())
    }

    @Transactional
    fun deleteComment(commentId: Long?, memberId: Long?) {
        val member = findMemberById(memberId)
        val comment = findCommentById(commentId)

        if (!comment.isAuthor(member)) {
            throw ValidationException("댓글을 삭제할 권한이 없습니다.")
        }


        // 이미 삭제된 댓글인지 확인
        if (comment.isDeleted()) {
            throw ValidationException("이미 삭제된 댓글입니다.")
        }

        comment.markAsDeleted()
    }

    @Transactional(readOnly = true)
    fun getCommentsByMember(memberId: Long?, page: Int, size: Int, currentMemberId: Long?): CommentListResponse {
        val targetMember = findMemberById(memberId)
        findMemberById(currentMemberId)

        val comments: List<Comment> = commentRepository.findCommentsByMember(targetMember, page, size)
        val commentResponses: List<CommentResponse> = CommentResponse.fromList(comments, currentMemberId)

        val hasNext = comments.size == size

        return CommentListResponse.of(commentResponses, comments.size.toLong(), 0L, page, size, hasNext)
    }

    @Transactional(readOnly = true)
    fun getRecentComments(limit: Int, memberId: Long?): CommentListResponse {
        findMemberById(memberId)

        val comments: List<Comment> = commentRepository.findRecentComments(limit)
        val commentResponses: List<CommentResponse> = CommentResponse.fromList(comments, memberId)

        return CommentListResponse.of(commentResponses, comments.size.toLong(), 0L, 0, limit, false)
    }

    private fun findMemberById(memberId: Long?): Member {
        return memberRepository.findMemberById(memberId)
            .orElseThrow<NotFoundException?>(Supplier { NotFoundException("회원을 찾을 수 없습니다.") })
    }

    private fun findMemoryById(memoryId: Long?): Memory {
        return memoryRepository.findMemoryById(memoryId)
            .orElseThrow<NotFoundException?>(Supplier { NotFoundException("메모리를 찾을 수 없습니다.") })
    }

    private fun findCommentById(commentId: Long?): Comment {
        return commentRepository.findCommentById(commentId)
            .orElseThrow<NotFoundException?>(Supplier { NotFoundException("댓글을 찾을 수 없습니다.") })
    }
}