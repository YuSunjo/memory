package com.memory.domain.hashtag.repository;

import com.memory.domain.hashtag.MemoryHashTag;
import com.memory.domain.memory.Memory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.memory.domain.hashtag.QMemoryHashTag.memoryHashTag;
import static com.memory.domain.memory.QMemory.memory;

@RequiredArgsConstructor
public class MemoryHashTagRepositoryCustomImpl implements MemoryHashTagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemoryHashTag> findByMemory(Memory memory) {
        return queryFactory.selectFrom(memoryHashTag)
                .where(
                        memoryHashTag.deleteDate.isNull(),
                        memoryHashTag.memory.eq(memory)
                )
                .fetch();
    }

    @Override
    public void deleteByMemory(Memory memory) {
        queryFactory.update(memoryHashTag)
                .set(memoryHashTag.deleteDate, java.time.LocalDateTime.now())
                .where(
                        memoryHashTag.deleteDate.isNull(),
                        memoryHashTag.memory.eq(memory)
                )
                .execute();
    }

    private BooleanExpression ltMemoryId(Long lastMemoryId) {
        return lastMemoryId != null ? memory.id.lt(lastMemoryId) : null;
    }
}