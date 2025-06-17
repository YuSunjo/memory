package com.memory.domain.calendar.repository;

import com.memory.domain.calendar.AnniversaryEvent;
import com.memory.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.memory.domain.calendar.QAnniversaryEvent.*;

@RequiredArgsConstructor
public class AnniversaryEventRepositoryCustomImpl implements AnniversaryEventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<AnniversaryEvent> findByMemberAndStartDateTimeBetween(Member member, LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory.selectFrom(anniversaryEvent)
                .where(
                        anniversaryEvent.member.eq(member),
                        anniversaryEvent.startDateTime.between(startDate, endDate),
                        anniversaryEvent.deleteDate.isNull()
                )
                .fetch();
    }

}
