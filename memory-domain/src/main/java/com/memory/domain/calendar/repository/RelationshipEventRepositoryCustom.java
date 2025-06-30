package com.memory.domain.calendar.repository;

import com.memory.domain.calendar.RelationshipEvent;
import com.memory.domain.member.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface RelationshipEventRepositoryCustom {

    List<RelationshipEvent> findByMemberAndStartDateTimeBetween(Member member, LocalDateTime startDate, LocalDateTime endDate);

    List<RelationshipEvent> findByMemberAndFutureEvents(Member member);
}