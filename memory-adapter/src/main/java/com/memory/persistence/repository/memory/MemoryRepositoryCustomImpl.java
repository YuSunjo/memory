package com.memory.persistence.repository.memory;

import com.memory.domain.memory.Memory;
import com.memory.domain.member.Member;
import com.memory.domain.memory.MemoryType;
import com.memory.domain.memory.repository.MemoryRepositoryCustom;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.memory.domain.memory.QMemory.memory;
import static com.memory.domain.file.QFile.file;

@Repository
@RequiredArgsConstructor
public class MemoryRepositoryCustomImpl implements MemoryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Memory> findByMemberAndMemoryType(Member member, List<Long> relatedMemberIds, MemoryType memoryType, int size) {
        return queryFactory.selectFrom(memory)
                .where(
                        getMemoryAccessCondition(member, relatedMemberIds, memoryType),
                        memory.deleteDate.isNull()
                )
                .orderBy(memory.id.desc())
                .limit(size)
                .fetch();
    }

    @Override
    public List<Memory> findByMemberAndMemoryType(Member member, List<Long> relatedMemberIds, MemoryType memoryType, Long lastMemoryId, int size) {
        return queryFactory.selectFrom(memory)
                .where(
                        getMemoryAccessCondition(member, relatedMemberIds, memoryType),
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

    // RELATIONSHIP: 내꺼 PRIVATE 제외 + 상대방 PRIVATE 제외
    // PUBLIC: 내꺼 PUBLIC만
    // PRIVATE: 내꺼 PRIVATE만
    private BooleanExpression getMemoryAccessCondition(Member member, List<Long> relatedMemberIds, MemoryType memoryType) {
        return switch (memoryType) {
            case RELATIONSHIP -> memory.member.eq(member)
                    .and(memory.memoryType.ne(MemoryType.PRIVATE))
                    .or(memory.member.id.in(relatedMemberIds)
                            .and(memory.memoryType.ne(MemoryType.PRIVATE)));
            case PRIVATE -> memory.member.eq(member)
                    .and(memory.memoryType.eq(MemoryType.PRIVATE));
            default -> memory.member.eq(member)
                    .and(memory.memoryType.eq(MemoryType.PUBLIC));
        };
    }
}
