package com.memory.domain.diary.repository;

import com.memory.domain.diary.Diary;
import com.memory.domain.member.Member;

import java.time.LocalDate;
import java.util.List;

public interface DiaryRepositoryCustom {

    List<Diary> findActiveDiariesByMemberAndDateBetween(Member member, LocalDate startDate, LocalDate endDate);
}