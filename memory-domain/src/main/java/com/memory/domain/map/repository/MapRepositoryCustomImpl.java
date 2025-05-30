package com.memory.domain.map.repository;

import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.memory.domain.map.QMap.map;

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
}