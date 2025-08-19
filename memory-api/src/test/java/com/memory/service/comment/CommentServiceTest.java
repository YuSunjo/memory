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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.MockSettings;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    private static final MockSettings LENIENT = withSettings().lenient();

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemoryRepository memoryRepository;

    @InjectMocks
    private CommentService commentService;

    private Member member;
    private Member otherMember;
    private Memory memory;
    private Comment topLevelComment;
    private Comment replyComment;
    private Comment deletedComment;
    private CommentCreateRequest createRequest;
    private CommentCreateRequest replyCreateRequest;
    private CommentUpdateRequest updateRequest;

    private final Long memberId = 1L;
    private final Long otherMemberId = 2L;
    private final Long memoryId = 1L;
    private final Long commentId = 1L;
    private final Long replyCommentId = 2L;
    private final String commentContent = "테스트 댓글 내용";
    private final String replyContent = "테스트 대댓글 내용";

    @BeforeEach
    void setUp() {
        member = new Member("테스트 사용자", "testuser", "test@example.com", "encodedPassword");
        setId(member, memberId);

        otherMember = new Member("다른 사용자", "otheruser", "other@example.com", "encodedPassword");
        setId(otherMember, otherMemberId);

        memory = mock(Memory.class, LENIENT);

        topLevelComment = mock(Comment.class, LENIENT);
        replyComment = mock(Comment.class, LENIENT);
        deletedComment = mock(Comment.class, LENIENT);

        createRequest = new CommentCreateRequest(memoryId, commentContent, null);
        replyCreateRequest = new CommentCreateRequest(memoryId, replyContent, commentId);
        updateRequest = new CommentUpdateRequest("수정된 댓글 내용");
    }

    private void setId(Object entity, Long id) {
        try {
            Field idField = entity.getClass().getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (Exception e) {
            throw new RuntimeException("Failed to set ID", e);
        }
    }

    @Test
    @DisplayName("최상위 댓글 생성 성공 테스트")
    void createTopLevelCommentSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryById(memoryId)).thenReturn(Optional.of(memory));
        
        when(topLevelComment.getId()).thenReturn(commentId);
        when(topLevelComment.getContent()).thenReturn(commentContent);
        when(topLevelComment.getDepth()).thenReturn(0);
        when(topLevelComment.getMemory()).thenReturn(memory);
        when(topLevelComment.getMember()).thenReturn(member);
        when(topLevelComment.getParent()).thenReturn(null);
        when(topLevelComment.getChildren()).thenReturn(Collections.emptyList());
        when(topLevelComment.isDeleted()).thenReturn(false);
        when(topLevelComment.getCreateDate()).thenReturn(java.time.LocalDateTime.now());
        when(topLevelComment.getUpdateDate()).thenReturn(java.time.LocalDateTime.now());
        
        when(commentRepository.save(any(Comment.class))).thenReturn(topLevelComment);

        // When
        CommentResponse response = commentService.createComment(createRequest, memberId);

        // Then
        assertNotNull(response);
        assertEquals(commentId, response.getId());
        assertEquals(commentContent, response.getContent());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryById(memoryId);
        verify(commentRepository).save(any(Comment.class));
        verify(memory).addComment(any(Comment.class));
    }

    @Test
    @DisplayName("대댓글 생성 성공 테스트")
    void createReplyCommentSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryById(memoryId)).thenReturn(Optional.of(memory));
        
        // 실제 Comment 생성 - Mock의 제약을 피하기 위해
        Comment realParentComment = Comment.create(commentContent, memory, member, null);
        setId(realParentComment, commentId);
        
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(realParentComment));
        when(memory.getId()).thenReturn(memoryId);
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment savedComment = invocation.getArgument(0);
            setId(savedComment, replyCommentId);
            return savedComment;
        });

        // When
        CommentResponse response = commentService.createComment(replyCreateRequest, memberId);

        // Then
        assertNotNull(response);
        assertEquals(replyCommentId, response.getId());
        assertEquals(replyContent, response.getContent());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryById(memoryId);
        verify(commentRepository).findCommentById(commentId);
        verify(commentRepository).save(any(Comment.class));
        verify(memory).addComment(any(Comment.class));
    }

    @Test
    @DisplayName("댓글 생성 실패 테스트 - 존재하지 않는 회원")
    void createCommentFailMemberNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.createComment(createRequest, memberId));

        assertEquals("회원을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository, never()).findMemoryById(anyLong());
    }

    @Test
    @DisplayName("댓글 생성 실패 테스트 - 존재하지 않는 메모리")
    void createCommentFailMemoryNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryById(memoryId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.createComment(createRequest, memberId));

        assertEquals("메모리를 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryById(memoryId);
    }

    @Test
    @DisplayName("대댓글 생성 실패 테스트 - 존재하지 않는 부모 댓글")
    void createReplyCommentFailParentNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryById(memoryId)).thenReturn(Optional.of(memory));
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.createComment(replyCreateRequest, memberId));

        assertEquals("댓글을 찾을 수 없습니다.", exception.getMessage());
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryById(memoryId);
        verify(commentRepository).findCommentById(commentId);
    }

    @Test
    @DisplayName("대댓글 생성 실패 테스트 - 대댓글을 달 수 없는 댓글")
    void createReplyCommentFailCannotHaveReply() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryById(memoryId)).thenReturn(Optional.of(memory));
        
        when(replyComment.getMemory()).thenReturn(memory);
        when(replyComment.canHaveReply()).thenReturn(false);
        when(replyComment.isDeleted()).thenReturn(false);
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(replyComment));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> commentService.createComment(replyCreateRequest, memberId));

        assertEquals("해당 댓글에는 더 이상 답글을 달 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("대댓글 생성 실패 테스트 - 삭제된 댓글에 답글")
    void createReplyCommentFailDeletedParent() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryById(memoryId)).thenReturn(Optional.of(memory));
        
        when(deletedComment.getMemory()).thenReturn(memory);
        when(deletedComment.canHaveReply()).thenReturn(true);
        when(deletedComment.isDeleted()).thenReturn(true);
        when(memory.getId()).thenReturn(memoryId);
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(deletedComment));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> commentService.createComment(replyCreateRequest, memberId));

        assertEquals("삭제된 댓글에는 답글을 달 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("메모리별 최상위 댓글 조회 성공 테스트")
    void getTopLevelCommentsByMemorySuccess() {
        // Given
        int page = 0;
        int size = 10;
        
        when(topLevelComment.getId()).thenReturn(commentId);
        when(topLevelComment.getContent()).thenReturn(commentContent);
        when(topLevelComment.getDepth()).thenReturn(0);
        when(topLevelComment.getMemory()).thenReturn(memory);
        when(topLevelComment.getMember()).thenReturn(member);
        when(topLevelComment.getParent()).thenReturn(null);
        when(topLevelComment.getChildren()).thenReturn(Collections.emptyList());
        when(topLevelComment.isDeleted()).thenReturn(false);
        when(topLevelComment.getCreateDate()).thenReturn(java.time.LocalDateTime.now());
        when(topLevelComment.getUpdateDate()).thenReturn(java.time.LocalDateTime.now());
        
        List<Comment> comments = List.of(topLevelComment);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryById(memoryId)).thenReturn(Optional.of(memory));
        when(commentRepository.findTopLevelCommentsByMemory(memory, page, size)).thenReturn(comments);
        when(commentRepository.countActiveCommentsByMemory(memory)).thenReturn(1L);
        when(memory.getTopLevelCommentsCount()).thenReturn(1L);

        // When
        CommentListResponse response = commentService.getTopLevelCommentsByMemory(memoryId, page, size, memberId);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(memoryRepository).findMemoryById(memoryId);
        verify(commentRepository).findTopLevelCommentsByMemory(memory, page, size);
        verify(commentRepository).countActiveCommentsByMemory(memory);
    }

    @Test
    @DisplayName("공개 메모리 댓글 조회 성공 테스트")
    void getTopLevelCommentsByPublicMemorySuccess() {
        // Given
        int page = 0;
        int size = 10;
        
        when(topLevelComment.getId()).thenReturn(commentId);
        when(topLevelComment.getContent()).thenReturn(commentContent);
        when(topLevelComment.getDepth()).thenReturn(0);
        when(topLevelComment.getMemory()).thenReturn(memory);
        when(topLevelComment.getMember()).thenReturn(member);
        when(topLevelComment.getParent()).thenReturn(null);
        when(topLevelComment.getChildren()).thenReturn(Collections.emptyList());
        when(topLevelComment.isDeleted()).thenReturn(false);
        when(topLevelComment.getCreateDate()).thenReturn(java.time.LocalDateTime.now());
        when(topLevelComment.getUpdateDate()).thenReturn(java.time.LocalDateTime.now());
        
        List<Comment> comments = List.of(topLevelComment);
        when(memoryRepository.findMemoryById(memoryId)).thenReturn(Optional.of(memory));
        when(commentRepository.findTopLevelCommentsByMemory(memory, page, size)).thenReturn(comments);
        when(commentRepository.countActiveCommentsByMemory(memory)).thenReturn(1L);
        when(memory.getTopLevelCommentsCount()).thenReturn(1L);

        // When
        CommentListResponse response = commentService.getTopLevelCommentsByPublicMemory(memoryId, page, size);

        // Then
        assertNotNull(response);
        verify(memoryRepository).findMemoryById(memoryId);
        verify(commentRepository).findTopLevelCommentsByMemory(memory, page, size);
        verify(commentRepository).countActiveCommentsByMemory(memory);
    }

    @Test
    @DisplayName("댓글별 답글 조회 성공 테스트")
    void getRepliesByCommentSuccess() {
        // Given
        when(replyComment.getId()).thenReturn(replyCommentId);
        when(replyComment.getContent()).thenReturn(replyContent);
        when(replyComment.getDepth()).thenReturn(1);
        when(replyComment.getMemory()).thenReturn(memory);
        when(replyComment.getMember()).thenReturn(member);
        when(replyComment.getParent()).thenReturn(topLevelComment);
        when(replyComment.getChildren()).thenReturn(Collections.emptyList());
        when(replyComment.isDeleted()).thenReturn(false);
        when(replyComment.getCreateDate()).thenReturn(java.time.LocalDateTime.now());
        when(replyComment.getUpdateDate()).thenReturn(java.time.LocalDateTime.now());
        
        List<Comment> replies = List.of(replyComment);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(topLevelComment));
        when(commentRepository.findRepliesByParentComment(topLevelComment)).thenReturn(replies);

        // When
        CommentListResponse response = commentService.getRepliesByComment(commentId, memberId);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(commentRepository).findCommentById(commentId);
        verify(commentRepository).findRepliesByParentComment(topLevelComment);
    }

    @Test
    @DisplayName("특정 댓글 조회 성공 테스트")
    void getCommentSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        
        when(topLevelComment.getId()).thenReturn(commentId);
        when(topLevelComment.getContent()).thenReturn(commentContent);
        when(topLevelComment.getDepth()).thenReturn(0);
        when(topLevelComment.getMemory()).thenReturn(memory);
        when(topLevelComment.getMember()).thenReturn(member);
        when(topLevelComment.getParent()).thenReturn(null);
        when(topLevelComment.getChildren()).thenReturn(Collections.emptyList());
        when(topLevelComment.isDeleted()).thenReturn(false);
        when(topLevelComment.getCreateDate()).thenReturn(java.time.LocalDateTime.now());
        when(topLevelComment.getUpdateDate()).thenReturn(java.time.LocalDateTime.now());
        
        when(commentRepository.findByIdWithMember(commentId)).thenReturn(Optional.of(topLevelComment));

        // When
        CommentResponse response = commentService.getComment(commentId, memberId);

        // Then
        assertNotNull(response);
        assertEquals(commentId, response.getId());
        assertEquals(commentContent, response.getContent());
        verify(memberRepository).findMemberById(memberId);
        verify(commentRepository).findByIdWithMember(commentId);
    }

    @Test
    @DisplayName("특정 댓글 조회 실패 테스트 - 존재하지 않는 댓글")
    void getCommentFailNotFound() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(commentRepository.findByIdWithMember(commentId)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
                () -> commentService.getComment(commentId, memberId));

        assertEquals("댓글을 찾을 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    void updateCommentSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        
        when(topLevelComment.getId()).thenReturn(commentId);
        when(topLevelComment.getContent()).thenReturn("수정된 댓글 내용");
        when(topLevelComment.getDepth()).thenReturn(0);
        when(topLevelComment.getMemory()).thenReturn(memory);
        when(topLevelComment.getMember()).thenReturn(member);
        when(topLevelComment.getParent()).thenReturn(null);
        when(topLevelComment.getChildren()).thenReturn(Collections.emptyList());
        when(topLevelComment.isDeleted()).thenReturn(false);
        when(topLevelComment.isAuthor(member)).thenReturn(true);
        when(topLevelComment.getCreateDate()).thenReturn(java.time.LocalDateTime.now());
        when(topLevelComment.getUpdateDate()).thenReturn(java.time.LocalDateTime.now());
        
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(topLevelComment));

        // When
        CommentResponse response = commentService.updateComment(commentId, updateRequest, memberId);

        // Then
        assertNotNull(response);
        assertEquals(commentId, response.getId());
        verify(memberRepository).findMemberById(memberId);
        verify(commentRepository).findCommentById(commentId);
        verify(topLevelComment).updateContent(updateRequest.getContent());
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트 - 권한 없음")
    void updateCommentFailNoPermission() {
        // Given
        when(memberRepository.findMemberById(otherMemberId)).thenReturn(Optional.of(otherMember));
        
        when(topLevelComment.isDeleted()).thenReturn(false);
        when(topLevelComment.isAuthor(otherMember)).thenReturn(false);
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(topLevelComment));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> commentService.updateComment(commentId, updateRequest, otherMemberId));

        assertEquals("댓글을 수정할 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("댓글 수정 실패 테스트 - 삭제된 댓글")
    void updateCommentFailDeletedComment() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(deletedComment.isAuthor(member)).thenReturn(true);
        when(deletedComment.isDeleted()).thenReturn(true);
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(deletedComment));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> commentService.updateComment(commentId, updateRequest, memberId));

        assertEquals("삭제된 댓글은 수정할 수 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    void deleteCommentSuccess() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(topLevelComment.isAuthor(member)).thenReturn(true);
        when(topLevelComment.isDeleted()).thenReturn(false);
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(topLevelComment));

        // When
        assertDoesNotThrow(() -> commentService.deleteComment(commentId, memberId));

        // Then
        verify(memberRepository).findMemberById(memberId);
        verify(commentRepository).findCommentById(commentId);
        verify(topLevelComment).markAsDeleted();
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트 - 권한 없음")
    void deleteCommentFailNoPermission() {
        // Given
        when(memberRepository.findMemberById(otherMemberId)).thenReturn(Optional.of(otherMember));
        when(topLevelComment.isAuthor(otherMember)).thenReturn(false);
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(topLevelComment));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> commentService.deleteComment(commentId, otherMemberId));

        assertEquals("댓글을 삭제할 권한이 없습니다.", exception.getMessage());
    }

    @Test
    @DisplayName("댓글 삭제 실패 테스트 - 이미 삭제된 댓글")
    void deleteCommentFailAlreadyDeleted() {
        // Given
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(deletedComment.isAuthor(member)).thenReturn(true);
        when(deletedComment.isDeleted()).thenReturn(true);
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(deletedComment));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> commentService.deleteComment(commentId, memberId));

        assertEquals("이미 삭제된 댓글입니다.", exception.getMessage());
    }

    @Test
    @DisplayName("회원별 댓글 조회 성공 테스트")
    void getCommentsByMemberSuccess() {
        // Given
        int page = 0;
        int size = 10;
        
        when(topLevelComment.getId()).thenReturn(commentId);
        when(topLevelComment.getContent()).thenReturn(commentContent);
        when(topLevelComment.getDepth()).thenReturn(0);
        when(topLevelComment.getMemory()).thenReturn(memory);
        when(topLevelComment.getMember()).thenReturn(member);
        when(topLevelComment.getParent()).thenReturn(null);
        when(topLevelComment.getChildren()).thenReturn(Collections.emptyList());
        when(topLevelComment.isDeleted()).thenReturn(false);
        when(topLevelComment.getCreateDate()).thenReturn(java.time.LocalDateTime.now());
        when(topLevelComment.getUpdateDate()).thenReturn(java.time.LocalDateTime.now());
        
        List<Comment> comments = List.of(topLevelComment);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memberRepository.findMemberById(otherMemberId)).thenReturn(Optional.of(otherMember));
        when(commentRepository.findCommentsByMember(member, page, size)).thenReturn(comments);

        // When
        CommentListResponse response = commentService.getCommentsByMember(memberId, page, size, otherMemberId);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(memberRepository).findMemberById(otherMemberId);
        verify(commentRepository).findCommentsByMember(member, page, size);
    }

    @Test
    @DisplayName("최근 댓글 조회 성공 테스트")
    void getRecentCommentsSuccess() {
        // Given
        int limit = 5;
        
        when(topLevelComment.getId()).thenReturn(commentId);
        when(topLevelComment.getContent()).thenReturn(commentContent);
        when(topLevelComment.getDepth()).thenReturn(0);
        when(topLevelComment.getMemory()).thenReturn(memory);
        when(topLevelComment.getMember()).thenReturn(member);
        when(topLevelComment.getParent()).thenReturn(null);
        when(topLevelComment.getChildren()).thenReturn(Collections.emptyList());
        when(topLevelComment.isDeleted()).thenReturn(false);
        when(topLevelComment.getCreateDate()).thenReturn(java.time.LocalDateTime.now());
        when(topLevelComment.getUpdateDate()).thenReturn(java.time.LocalDateTime.now());
        
        List<Comment> comments = List.of(topLevelComment);
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(commentRepository.findRecentComments(limit)).thenReturn(comments);

        // When
        CommentListResponse response = commentService.getRecentComments(limit, memberId);

        // Then
        assertNotNull(response);
        verify(memberRepository).findMemberById(memberId);
        verify(commentRepository).findRecentComments(limit);
    }

    @Test
    @DisplayName("빈 댓글 목록 조회 테스트")
    void getCommentsEmptyList() {
        // Given
        int page = 0;
        int size = 10;
        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryById(memoryId)).thenReturn(Optional.of(memory));
        when(commentRepository.findTopLevelCommentsByMemory(memory, page, size)).thenReturn(Collections.emptyList());
        when(commentRepository.countActiveCommentsByMemory(memory)).thenReturn(0L);
        when(memory.getTopLevelCommentsCount()).thenReturn(0L);

        // When
        CommentListResponse response = commentService.getTopLevelCommentsByMemory(memoryId, page, size, memberId);

        // Then
        assertNotNull(response);
        verify(commentRepository).findTopLevelCommentsByMemory(memory, page, size);
    }

    @Test
    @DisplayName("댓글 생성 시 다른 메모리의 부모 댓글 실패 테스트")
    void createReplyCommentFailDifferentMemory() {
        // Given
        Memory otherMemory = mock(Memory.class, LENIENT);
        when(otherMemory.getId()).thenReturn(2L);
        
        when(topLevelComment.getMemory()).thenReturn(otherMemory);
        when(topLevelComment.canHaveReply()).thenReturn(true);
        when(topLevelComment.isDeleted()).thenReturn(false);

        when(memberRepository.findMemberById(memberId)).thenReturn(Optional.of(member));
        when(memoryRepository.findMemoryById(memoryId)).thenReturn(Optional.of(memory));
        when(commentRepository.findCommentById(commentId)).thenReturn(Optional.of(topLevelComment));

        // When & Then
        ValidationException exception = assertThrows(ValidationException.class,
                () -> commentService.createComment(replyCreateRequest, memberId));

        assertEquals("댓글과 대댓글은 같은 메모리에 속해야 합니다.", exception.getMessage());
    }
}