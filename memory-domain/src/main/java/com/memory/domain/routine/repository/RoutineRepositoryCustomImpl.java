package com.memory.domain.routine.repository;

import com.memory.domain.member.Member;
import com.memory.domain.routine.Routine;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import static com.memory.domain.routine.QRoutine.routine;

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

    @Override
    public boolean existsByMemberAndTitleAndRepeatType(Member member, String title, String repeatType) {
        Integer exists = queryFactory
                .selectOne()
                .from(routine)
                .where(
                        routine.member.eq(member)
                        .and(routine.title.eq(title))
                        .and(routine.repeatSetting.repeatType.stringValue().eq(repeatType))
                        .and(routine.deleteDate.isNull())
                )
                .fetchFirst();
        
        return exists != null;
    }

    @Override
    public long countActiveRoutinesByMember(Member member) {
        return queryFactory
                .selectFrom(routine)
                .where(
                        routine.member.eq(member)
                        .and(routine.active.isTrue())
                        .and(routine.deleteDate.isNull())
                )
                .stream().count();
    }

    @Override
    public List<Routine> findActiveRoutinesByMemberAndRepeatType(Member member, String repeatType) {
        return queryFactory
                .selectFrom(routine)
                .where(
                        routine.member.eq(member)
                        .and(routine.active.isTrue())
                        .and(routine.repeatSetting.repeatType.stringValue().eq(repeatType))
                        .and(routine.deleteDate.isNull())
                )
                .orderBy(
                        routine.createDate.desc()
                )
                .fetch();
    }
}
