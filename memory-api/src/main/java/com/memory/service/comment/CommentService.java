package com.memory.service.comment;

import com.memory.domain.comment.Comment;
import com.memory.domain.comment.repository.CommentRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.memory.Memory;
import com.memory.domain.memory.repository.MemoryRepository;
import com.memory.dto.comment.CommentCreateRequest;
import com.memory.dto.comment.CommentListResponse;
import com.memory.dto.comment.CommentResponse;
import com.memory.dto.comment.CommentUpdateRequest;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final MemoryRepository memoryRepository;

    @Transactional
    public CommentResponse createComment(CommentCreateRequest request, Long memberId) {
        Member member = findMemberById(memberId);
        Memory memory = findMemoryById(request.getMemoryId());
        
        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = findCommentById(request.getParentCommentId());
            
            // 대댓글을 달 수 있는지 확인
            if (!parentComment.canHaveReply()) {
                throw new ValidationException("해당 댓글에는 더 이상 답글을 달 수 없습니다.");
            }
            
            // 부모 댓글이 같은 메모리에 속하는지 확인
            if (!parentComment.getMemory().getId().equals(memory.getId())) {
                throw new ValidationException("댓글과 대댓글은 같은 메모리에 속해야 합니다.");
            }
            
            // 부모 댓글이 삭제되었는지 확인
            if (parentComment.isDeleted()) {
                throw new ValidationException("삭제된 댓글에는 답글을 달 수 없습니다.");
            }
        }

        Comment comment = request.toEntity(member, memory, parentComment);
        Comment savedComment = commentRepository.save(comment);
        memory.addComment(savedComment);

        return CommentResponse.from(savedComment, memberId, false);
    }

    @Transactional(readOnly = true)
    public CommentListResponse getTopLevelCommentsByMemory(Long memoryId, int page, int size, Long memberId) {
        findMemberById(memberId);
        Memory memory = findMemoryById(memoryId);
        
        List<Comment> comments = commentRepository.findTopLevelCommentsByMemory(memory, page, size);
        System.out.println("comments = " + comments);
        List<CommentResponse> commentResponses = CommentResponse.fromList(comments, memberId);
        
        long totalCount = commentRepository.countActiveCommentsByMemory(memory);
        long topLevelCount = memory.getTopLevelCommentsCount();
        boolean hasNext = comments.size() == size;
        
        return CommentListResponse.of(commentResponses, totalCount, topLevelCount, page, size, hasNext);
    }

    @Transactional(readOnly = true)
    public CommentListResponse getRepliesByComment(Long commentId, Long memberId) {
        findMemberById(memberId);
        Comment parentComment = findCommentById(commentId);
        
        List<Comment> replies = commentRepository.findRepliesByParentComment(parentComment);
        List<CommentResponse> replyResponses = CommentResponse.fromList(replies, memberId);
        
        return CommentListResponse.of(replyResponses, (long) replies.size(), 0L, 0, replies.size(), false);
    }

    @Transactional(readOnly = true)
    public CommentResponse getComment(Long commentId, Long memberId) {
        findMemberById(memberId);
        Comment comment = commentRepository.findByIdWithMember(commentId)
                .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));
        
        return CommentResponse.from(comment, memberId, true);
    }

    @Transactional
    public CommentResponse updateComment(Long commentId, CommentUpdateRequest request, Long memberId) {
        Member member = findMemberById(memberId);
        Comment comment = findCommentById(commentId);
        
        if (!comment.isAuthor(member)) {
            throw new ValidationException("댓글을 수정할 권한이 없습니다.");
        }
        
        // 삭제된 댓글인지 확인
        if (comment.isDeleted()) {
            throw new ValidationException("삭제된 댓글은 수정할 수 없습니다.");
        }
        
        comment.updateContent(request.getContent());
        
        return CommentResponse.from(comment, memberId, !comment.getChildren().isEmpty());
    }

    @Transactional
    public void deleteComment(Long commentId, Long memberId) {
        Member member = findMemberById(memberId);
        Comment comment = findCommentById(commentId);
        
        if (!comment.isAuthor(member)) {
            throw new ValidationException("댓글을 삭제할 권한이 없습니다.");
        }
        
        // 이미 삭제된 댓글인지 확인
        if (comment.isDeleted()) {
            throw new ValidationException("이미 삭제된 댓글입니다.");
        }
        
        comment.markAsDeleted();
    }

    @Transactional(readOnly = true)
    public CommentListResponse getCommentsByMember(Long memberId, int page, int size, Long currentMemberId) {
        Member targetMember = findMemberById(memberId);
        findMemberById(currentMemberId);
        
        List<Comment> comments = commentRepository.findCommentsByMember(targetMember, page, size);
        List<CommentResponse> commentResponses = CommentResponse.fromList(comments, currentMemberId);
        
        boolean hasNext = comments.size() == size;
        
        return CommentListResponse.of(commentResponses, (long) comments.size(), 0L, page, size, hasNext);
    }

    @Transactional(readOnly = true)
    public CommentListResponse getRecentComments(int limit, Long memberId) {
        findMemberById(memberId);
        
        List<Comment> comments = commentRepository.findRecentComments(limit);
        List<CommentResponse> commentResponses = CommentResponse.fromList(comments, memberId);
        
        return CommentListResponse.of(commentResponses, (long) comments.size(), 0L, 0, limit, false);
    }

    private Member findMemberById(Long memberId) {
        return memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));
    }

    private Memory findMemoryById(Long memoryId) {
        return memoryRepository.findMemoryById(memoryId)
                .orElseThrow(() -> new NotFoundException("메모리를 찾을 수 없습니다."));
    }

    private Comment findCommentById(Long commentId) {
        return commentRepository.findCommentById(commentId)
                .orElseThrow(() -> new NotFoundException("댓글을 찾을 수 없습니다."));
    }
}
