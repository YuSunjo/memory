package com.memory.domain.memberlink.repository;

import com.memory.domain.memberlink.MemberLink;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.memory.domain.memberlink.QMemberLink.memberLink;

@Repository
@RequiredArgsConstructor
public class MemberLinkRepositoryCustomImpl implements MemberLinkRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<MemberLink> findActiveByMemberIdOrderByDisplayOrder(Long memberId) {
        return queryFactory
                .selectFrom(memberLink)
                .where(
                        memberLink.member.id.eq(memberId),
                        memberLink.deleteDate.isNull()
                )
                .orderBy(memberLink.displayOrder.asc())
                .fetch();
    }

    @Override
    public List<MemberLink> findPublicByMemberIdOrderByDisplayOrder(Long memberId) {
        return queryFactory
                .selectFrom(memberLink)
                .where(
                        memberLink.member.id.eq(memberId),
                        memberLink.isActive.isTrue(),
                        memberLink.isVisible.isTrue(),
                        memberLink.deleteDate.isNull()
                )
                .orderBy(memberLink.displayOrder.asc())
                .fetch();
    }

    @Override
    public Long countByMemberId(Long memberId) {
        return queryFactory
                .select(memberLink.count())
                .from(memberLink)
                .where(
                        memberLink.member.id.eq(memberId),
                        memberLink.deleteDate.isNull()
                )
                .fetchOne();
    }

    @Override
    public Integer findMaxDisplayOrderByMemberId(Long memberId) {
        Integer maxOrder = queryFactory
                .select(memberLink.displayOrder.max())
                .from(memberLink)
                .where(
                        memberLink.member.id.eq(memberId),
                        memberLink.deleteDate.isNull()
                )
                .fetchOne();
        
        return maxOrder != null ? maxOrder : 0;
    }

    @Override
    public Optional<MemberLink> findByIdAndMemberId(Long linkId, Long memberId) {
        MemberLink result = queryFactory
                .selectFrom(memberLink)
                .where(
                        memberLink.id.eq(linkId),
                        memberLink.member.id.eq(memberId),
                        memberLink.deleteDate.isNull()
                )
                .fetchOne();
        
        return Optional.ofNullable(result);
    }

    @Override
    public List<MemberLink> findByMemberIdAndDisplayOrderBetween(Long memberId, Integer startOrder, Integer endOrder) {
        return queryFactory
                .selectFrom(memberLink)
                .where(
                        memberLink.member.id.eq(memberId),
                        memberLink.displayOrder.between(startOrder, endOrder),
                        memberLink.deleteDate.isNull()
                )
                .orderBy(memberLink.displayOrder.asc())
                .fetch();
    }
}
