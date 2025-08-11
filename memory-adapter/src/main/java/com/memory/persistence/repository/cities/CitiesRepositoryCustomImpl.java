package com.memory.persistence.repository.cities;

import com.memory.domain.cities.Cities;
import com.memory.domain.cities.repository.CitiesRepositoryCustom;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import static com.memory.domain.cities.QCities.cities;

@Repository
@RequiredArgsConstructor
public class CitiesRepositoryCustomImpl implements CitiesRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    public Optional<Cities> findRandomCities() {
        return Optional.ofNullable(
                queryFactory
                        .selectFrom(cities)
                        .orderBy(Expressions.numberTemplate(Double.class, "RANDOM()").asc())
                        .fetchFirst()
        );
    }

}
