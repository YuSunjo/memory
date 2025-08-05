package com.memory.persistence.repository.routine;

import com.memory.domain.member.Member;
import com.memory.domain.routine.Routine;
import com.memory.domain.routine.repository.RoutineRepositoryCustom;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.memory.domain.routine.QRoutine.routine;

@Repository
@RequiredArgsConstructor
public class RoutineRepositoryCustomImpl implements RoutineRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Routine> findActiveRoutinesByMember(Member member) {
        return queryFactory
                .selectFrom(routine)
                .where(
                        routine.member.eq(member)
                        .and(routine.active.isTrue())
                        .and(routine.deleteDate.isNull())
                )
                .orderBy(
                        routine.repeatSetting.repeatType.asc(),
                        routine.createDate.desc()
                )
                .fetch();
    }

    @Override
    public List<Routine> findAllRoutinesByMember(Member member) {
        return queryFactory
                .selectFrom(routine)
                .where(
                        routine.member.eq(member)
                        .and(routine.deleteDate.isNull())
                )
                .orderBy(
                        routine.active.desc(),
                        routine.repeatSetting.repeatType.asc(),
                        routine.createDate.desc()
                )
                .fetch();
    }

    @Override
    public Optional<Routine> findByIdAndMember(Long id, Member member) {
        Routine result = queryFactory
                .selectFrom(routine)
                .where(
                        routine.id.eq(id)
                        .and(routine.member.eq(member))
                        .and(routine.deleteDate.isNull())
                )
                .fetchOne();
        
        return Optional.ofNullable(result);
    }
}
