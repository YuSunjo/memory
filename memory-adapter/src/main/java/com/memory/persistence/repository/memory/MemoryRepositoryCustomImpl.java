package com.memory.persistence.repository.memory;

import com.memory.domain.memory.Memory;
import com.memory.domain.member.Member;
import com.memory.domain.memory.MemoryType;
import com.memory.domain.memory.repository.MemoryRepositoryCustom;
import com.memory.domain.relationship.RelationshipStatus;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.memory.domain.memory.QMemory.memory;
import static com.memory.domain.file.QFile.file;
import static com.memory.domain.relationship.QRelationship.relationship;

@Repository
@RequiredArgsConstructor
public class MemoryRepositoryCustomImpl implements MemoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Memory> findByMemberAndMemoryType(Member member, MemoryType memoryType, int size) {
        return queryFactory.selectFrom(memory)
                .where(
                        getMemoryAccessCondition(member),
                        getMemoryType(memoryType),
                        memory.deleteDate.isNull()
                )
                .orderBy(memory.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Memory> findByMemberAndMemoryType(Member member, MemoryType memoryType, Long lastMemoryId, int size) {
        return queryFactory.selectFrom(memory)
                .where(
                        getMemoryAccessCondition(member),
                        getMemoryType(memoryType),
                        memory.deleteDate.isNull(),
                        ltMemoryId(lastMemoryId)
                )
                .orderBy(memory.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public Optional<Memory> findMemoryByIdAndMemberId(Long memoryId, Long memberId) {
        return Optional.ofNullable(queryFactory.selectFrom(memory)
                .where(
                        memory.id.eq(memoryId),
                        memory.member.id.eq(memberId),
                        memory.deleteDate.isNull()
                )
                .fetchOne());
    }

    @Override
    public List<Memory> findByMemoryType(MemoryType memoryType, int size) {
        return queryFactory.selectFrom(memory)
                .where(
                        memory.memoryType.eq(memoryType),
                        memory.deleteDate.isNull()
                )
                .orderBy(memory.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Memory> findByMemoryType(MemoryType memoryType, Long lastMemoryId, int size) {
        return queryFactory.selectFrom(memory)
                .where(
                        getMemoryType(memoryType),
                        memory.deleteDate.isNull(),
                        ltMemoryId(lastMemoryId)
                )
                .orderBy(memory.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Memory> findMemoriesWithImagesByMember(Member member) {
        return queryFactory.selectFrom(memory)
                .join(memory.files, file)
                .where(
                        memory.member.eq(member),
                        memory.deleteDate.isNull(),
                        file.deleteDate.isNull()
                )
                .distinct()
                .fetch();
    }

    @Override
    public List<Memory> findMemoriesWithImagesByMemoryType(MemoryType memoryType) {
        return queryFactory.selectFrom(memory)
                .join(memory.files, file)
                .where(
                        memory.memoryType.eq(memoryType),
                        memory.deleteDate.isNull(),
                        file.deleteDate.isNull(),
                        file.fileType.stringValue().like("IMAGE%")
                )
                .distinct()
                .fetch();
    }

    @Override
    public Optional<Memory> findMemoryById(Long memoryId) {
        return Optional.ofNullable(queryFactory.selectFrom(memory)
                .where(
                        memory.id.eq(memoryId),
                        memory.deleteDate.isNull()
                )
                .fetchOne());
    }

    private BooleanExpression ltMemoryId(Long memoryId) {
        return memoryId != null ? memory.id.lt(memoryId) : null;
    }

    private Predicate getMemoryType(MemoryType memoryType) {
        if (MemoryType.RELATIONSHIP.equals(memoryType)) {
            return memory.memoryType.eq(MemoryType.RELATIONSHIP).or(memory.memoryType.eq(MemoryType.PUBLIC));
        }
        return memory.memoryType.eq(memoryType);
    }

    /**
     * 나의 메모리 또는 나와 연결된 사람의 PUBLIC/RELATIONSHIP 타입 메모리를 조회하는 조건
     */
    private BooleanExpression getMemoryAccessCondition(Member member) {
        // 1. 나의 모든 메모리
        BooleanExpression myMemories = memory.member.eq(member);
        
        // 2. 연결된 사람들의 ID 서브쿼리 
        BooleanExpression connectedMembers = memory.member.id.in(
            queryFactory.select(relationship.relatedMember.id)
                    .from(relationship)
                    .where(
                            relationship.member.eq(member),
                            relationship.relationshipStatus.eq(RelationshipStatus.ACCEPTED)
                    )
        ).or(memory.member.id.in(
            queryFactory.select(relationship.member.id)
                    .from(relationship)
                    .where(
                            relationship.relatedMember.eq(member),
                            relationship.relationshipStatus.eq(RelationshipStatus.ACCEPTED)
                    )
        ));
        
        // 3. 연결된 사람들의 PUBLIC 또는 RELATIONSHIP 타입 메모리
        BooleanExpression connectedMembersPublicOrRelationshipMemories = connectedMembers
                .and(memory.memoryType.eq(MemoryType.PUBLIC)
                        .or(memory.memoryType.eq(MemoryType.RELATIONSHIP)));
        
        // 최종 조건: 나의 메모리 OR 연결된 사람들의 PUBLIC/RELATIONSHIP 메모리
        return myMemories.or(connectedMembersPublicOrRelationshipMemories);
    }
}
