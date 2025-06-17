package com.memory.domain.calendar.repository;

import com.memory.domain.calendar.AnniversaryEvent;
import com.memory.domain.member.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface AnniversaryEventRepositoryCustom {

    List<AnniversaryEvent> findByMemberAndStartDateTimeBetween(Member member, LocalDateTime startDate, LocalDateTime endDate);

}
