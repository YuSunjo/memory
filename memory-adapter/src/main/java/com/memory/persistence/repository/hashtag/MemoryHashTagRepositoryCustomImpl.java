package com.memory.persistence.repository.hashtag;

import com.memory.domain.hashtag.MemoryHashTag;
import com.memory.domain.hashtag.repository.MemoryHashTagRepositoryCustom;
import com.memory.domain.memory.Memory;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.memory.domain.hashtag.QMemoryHashTag.memoryHashTag;
import static com.memory.domain.memory.QMemory.memory;

@Repository
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
}