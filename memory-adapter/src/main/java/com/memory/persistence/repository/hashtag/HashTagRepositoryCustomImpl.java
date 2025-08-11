package com.memory.persistence.repository.hashtag;

import com.memory.domain.hashtag.HashTag;
import com.memory.domain.hashtag.repository.HashTagRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.memory.domain.hashtag.QHashTag.hashTag;

@Repository
@RequiredArgsConstructor
public class HashTagRepositoryCustomImpl implements HashTagRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<HashTag> findPopularHashTags(int limit) {
        return queryFactory.selectFrom(hashTag)
                .where(hashTag.deleteDate.isNull())
                .orderBy(hashTag.useCount.desc(), hashTag.createDate.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public List<HashTag> findHashTagsByNameContaining(String keyword, int limit) {
        return queryFactory.selectFrom(hashTag)
                .where(
                        hashTag.deleteDate.isNull(),
                        hashTag.name.containsIgnoreCase(keyword)
                )
                .orderBy(hashTag.useCount.desc(), hashTag.createDate.desc())
                .limit(limit)
                .fetch();
    }

    @Override
    public Optional<HashTag> findByName(String name) {
        HashTag result = queryFactory.selectFrom(hashTag)
                .where(
                        hashTag.deleteDate.isNull(),
                        hashTag.name.eq(name)
                )
                .fetchOne();
        
        return Optional.ofNullable(result);
    }
}