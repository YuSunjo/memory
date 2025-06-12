package com.memory.domain.memory.repository;

import com.memory.domain.memory.Memory;
import com.memory.domain.member.Member;
import com.memory.domain.memory.MemoryType;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.memory.domain.memory.QMemory.memory;

@RequiredArgsConstructor
public class MemoryRepositoryCustomImpl implements MemoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Memory> findByMemberAndMemoryType(Member member, MemoryType memoryType, int size) {
        return queryFactory.selectFrom(memory)
                .where(
                        memory.member.eq(member),
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
                        memory.member.eq(member),
                        getMemoryType(memoryType),
                        memory.deleteDate.isNull(),
                        gtMemoryId(lastMemoryId)
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
                        gtMemoryId(lastMemoryId)
                )
                .orderBy(memory.id.desc())
                .limit(size)
                .fetch();
    }

    private BooleanExpression gtMemoryId(Long memoryId) {
        return memoryId != null ? memory.id.gt(memoryId) : null;
    }

    private Predicate getMemoryType(MemoryType memoryType) {
        if (MemoryType.RELATIONSHIP.equals(memoryType)) {
            return memory.memoryType.eq(MemoryType.RELATIONSHIP).or(memory.memoryType.eq(MemoryType.PUBLIC));
        }
        return memory.memoryType.eq(memoryType);
    }
}
