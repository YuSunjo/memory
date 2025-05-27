package com.memory.domain.member.repository;

import com.memory.domain.member.Member;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

import static com.memory.domain.member.QMember.*;

@RequiredArgsConstructor
public class MemberRepositoryCustomImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Optional<Member> findMemberByEmail(String email) {
        return Optional.ofNullable(
            queryFactory.selectFrom(member)
                .where(
                    member.email.eq(email)
                )
                .fetchOne()
        );
    }

    @Override
    public Optional<Member> findMemberById(Long memberId) {
        return Optional.ofNullable(
            queryFactory.selectFrom(member)
                .where(
                    member.id.eq(memberId),
                    member.deleteDate.isNull()
                )
                .fetchOne()
        );
    }

}
