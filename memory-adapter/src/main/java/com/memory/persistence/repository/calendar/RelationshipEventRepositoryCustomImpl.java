package com.memory.persistence.repository.calendar;

import com.memory.domain.calendar.RelationshipEvent;
import com.memory.domain.calendar.repository.RelationshipEventRepositoryCustom;
import com.memory.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static com.memory.domain.calendar.QRelationshipEvent.*;

@Repository
@RequiredArgsConstructor
public class RelationshipEventRepositoryCustomImpl implements RelationshipEventRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<RelationshipEvent> findByMemberAndStartDateTimeBetween(Member member, LocalDateTime startDate, LocalDateTime endDate) {
        return queryFactory.selectFrom(relationshipEvent)
                .where(
                        relationshipEvent.member.eq(member)
                        .or(relationshipEvent.relationship.member.eq(member))
                        .or(relationshipEvent.relationship.relatedMember.eq(member)),
                        relationshipEvent.startDateTime.between(startDate, endDate),
                        relationshipEvent.deleteDate.isNull()
                )
                .orderBy(relationshipEvent.startDateTime.asc())
                .fetch();
    }

    @Override
    public List<RelationshipEvent> findByMemberAndFutureEvents(Member member) {
        return queryFactory.selectFrom(relationshipEvent)
                .where(
                        relationshipEvent.member.eq(member)
                        .or(relationshipEvent.relationship.member.eq(member))
                        .or(relationshipEvent.relationship.relatedMember.eq(member)),
                        relationshipEvent.startDateTime.goe(LocalDateTime.now()),
                        relationshipEvent.deleteDate.isNull()
                )
                .orderBy(relationshipEvent.startDateTime.asc())
                .fetch();
    }

}