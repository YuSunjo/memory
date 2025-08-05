package com.memory.persistence.repository.map;

import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;
import com.memory.domain.map.repository.MapRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.memory.domain.map.QMap.map;

@Repository
@RequiredArgsConstructor
public class MapRepositoryCustomImpl implements MapRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Map> findByMapType(MapType mapType) {
        return queryFactory.selectFrom(map)
                .where(
                        map.mapType.eq(mapType),
                        map.deleteDate.isNull()
                )
                .fetch();
    }

    @Override
    public List<Map> findByMemberId(Long memberId) {
        return queryFactory.selectFrom(map)
                .where(
                        map.member.id.eq(memberId),
                        map.mapType.eq(MapType.USER_PLACE),
                        map.deleteDate.isNull()
                )
                .fetch();
    }
}
