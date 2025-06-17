package com.memory.domain.calendar.repository;

import com.memory.domain.calendar.PersonalEvent;
import com.memory.domain.member.Member;

import java.time.LocalDateTime;
import java.util.List;

public interface PersonalEventRepositoryCustom {

    List<PersonalEvent> findByMemberAndStartDateTimeBetween(Member member, LocalDateTime startDate, LocalDateTime endDate);
    
}