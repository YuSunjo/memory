package com.memory.persistence.repository.comment;

import com.memory.domain.comment.Comment;
import com.memory.domain.comment.repository.CommentRepositoryCustom;
import com.memory.domain.member.Member;
import com.memory.domain.memory.Memory;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.memory.domain.comment.QComment.comment;
import static com.memory.domain.member.QMember.member;

@Repository
@RequiredArgsConstructor
public class CommentRepositoryCustomImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Comment> findCommentsByMemoryWithHierarchy(Memory memory) {
        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member, member).fetchJoin()
                .leftJoin(comment.parent).fetchJoin()
                .where(
                        comment.memory.eq(memory)
                                .and(comment.deleteDate.isNull())
                )
                .orderBy(
                        comment.parent.id.asc().nullsFirst(), // 최상위 댓글 먼저
                        comment.createDate.asc() // 생성 시간 순
                )
                .fetch();
    }

    @Override
    public List<Comment> findTopLevelCommentsByMemory(Memory memory, int page, int size) {
        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member, member).fetchJoin()
                .where(
                        comment.memory.eq(memory)
                                .and(comment.depth.eq(0))
                                .and(comment.deleteDate.isNull())
                )
                .orderBy(comment.createDate.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public List<Comment> findRepliesByParentComment(Comment parentComment) {
        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member, member).fetchJoin()
                .where(
                        comment.parent.eq(parentComment)
                                .and(comment.deleteDate.isNull())
                )
                .orderBy(comment.createDate.asc())
                .fetch();
    }

    @Override
    public long countActiveCommentsByMemory(Memory memory) {
        Long count = queryFactory
                .select(comment.count())
                .from(comment)
                .where(
                        comment.memory.eq(memory)
                                .and(comment.deleteDate.isNull())
                )
                .fetchOne();
        
        return count != null ? count : 0L;
    }

    @Override
    public List<Comment> findCommentsByMember(Member member, int page, int size) {
        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.memory).fetchJoin()
                .where(
                        comment.member.eq(member)
                                .and(comment.deleteDate.isNull())
                )
                .orderBy(comment.createDate.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    @Override
    public Optional<Comment> findByIdWithMember(Long commentId) {
        Comment result = queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member, member).fetchJoin()
                .where(comment.id.eq(commentId))
                .fetchOne();
        
        return Optional.ofNullable(result);
    }

    @Override
    public List<Comment> findRecentComments(int limit) {
        return queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member, member).fetchJoin()
                .leftJoin(comment.memory).fetchJoin()
                .where(comment.deleteDate.isNull())
                .orderBy(comment.createDate.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Optional<Comment> findCommentById(Long commentId) {
        return Optional.ofNullable(queryFactory
                .selectFrom(comment)
                .leftJoin(comment.member, member).fetchJoin()
                .where(comment.id.eq(commentId))
                .fetchOne());
    }
}
