package com.memory.persistence.repository.diary;

import com.memory.domain.diary.Diary;
import com.memory.domain.diary.repository.DiaryRepositoryCustom;
import com.memory.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static com.memory.domain.diary.QDiary.diary;

@Repository
@RequiredArgsConstructor
public class DiaryRepositoryCustomImpl implements DiaryRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Diary> findActiveDiariesByMemberAndDateBetween(Member member, LocalDate startDate, LocalDate endDate) {
        return queryFactory
                .selectFrom(diary)
                .where(
                        diary.member.eq(member)
                                .and(diary.date.between(startDate, endDate))
                                .and(diary.deleteDate.isNull())
                )
                .fetch();
    }
}