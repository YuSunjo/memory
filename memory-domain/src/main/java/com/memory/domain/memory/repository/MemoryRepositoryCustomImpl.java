package com.memory.domain.memory.repository;

import com.memory.domain.memory.Memory;
import com.memory.domain.member.Member;
import com.memory.domain.memory.MemoryType;
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
    public List<Memory> findByMemberAndMemoryType(Member member, MemoryType memoryType) {
        return queryFactory.selectFrom(memory)
                .where(
                        memory.member.eq(member),
                        memory.memoryType.eq(memoryType),
                        memory.deleteDate.isNull()
                )
                .orderBy(memory.id.desc())
                .fetch();
    }

    @Override
    public List<Memory> findByMemberAndMemoryType(Member member, MemoryType memoryType, Long lastMemoryId, int size) {
        return queryFactory.selectFrom(memory)
                .where(
                        memory.member.eq(member),
                        memory.memoryType.eq(memoryType),
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

    private BooleanExpression gtMemoryId(Long memoryId) {
        return memoryId != null ? memory.id.gt(memoryId) : null;
    }
}
