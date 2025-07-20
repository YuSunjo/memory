package com.memory.domain.comment.repository;

import com.memory.domain.comment.Comment;
import com.memory.domain.member.Member;
import com.memory.domain.memory.Memory;

import java.util.List;
import java.util.Optional;

public interface CommentRepositoryCustom {

    List<Comment> findCommentsByMemoryWithHierarchy(Memory memory);

    List<Comment> findTopLevelCommentsByMemory(Memory memory, int page, int size);

    List<Comment> findRepliesByParentComment(Comment parentComment);

    long countActiveCommentsByMemory(Memory memory);

    List<Comment> findCommentsByMember(Member member, int page, int size);

    Optional<Comment> findByIdWithMember(Long commentId);

    List<Comment> findRecentComments(int limit);

    Optional<Comment> findCommentById(Long commentId);
}
