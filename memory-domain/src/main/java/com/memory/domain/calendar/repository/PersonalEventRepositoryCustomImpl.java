package com.memory.domain.calendar.repository;

import com.memory.domain.calendar.PersonalEvent;
import com.memory.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.memory.domain.calendar.QPersonalEvent.*;

@RequiredArgsConstructor
public class PersonalEventRepositoryCustomImpl implements PersonalEventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<PersonalEvent> findByMemberAndStartDateTimeBetween(Member member, LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory.selectFrom(personalEvent)
                .where(
                        personalEvent.member.eq(member),
                        personalEvent.startDateTime.between(startDate, endDate),
                        personalEvent.deleteDate.isNull()
                )
                .orderBy(personalEvent.startDateTime.asc())
                .fetch();
    }

    @Override
    public List<PersonalEvent> findByMemberAndFutureEvents(Member member) {
        return queryFactory.selectFrom(personalEvent)
                .where(
                        personalEvent.member.eq(member),
                        personalEvent.startDateTime.goe(LocalDateTime.now()),
                        personalEvent.deleteDate.isNull()
                )
                .orderBy(personalEvent.startDateTime.asc())
                .fetch();
    }

}