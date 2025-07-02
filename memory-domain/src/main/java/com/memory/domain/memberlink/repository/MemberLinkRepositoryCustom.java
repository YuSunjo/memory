package com.memory.domain.memberlink.repository;

import com.memory.domain.memberlink.MemberLink;

import java.util.List;
import java.util.Optional;

public interface MemberLinkRepositoryCustom {

    List<MemberLink> findActiveByMemberIdOrderByDisplayOrder(Long memberId);

    List<MemberLink> findPublicByMemberIdOrderByDisplayOrder(Long memberId);

    Long countByMemberId(Long memberId);

    Integer findMaxDisplayOrderByMemberId(Long memberId);

    Optional<MemberLink> findByIdAndMemberId(Long linkId, Long memberId);

    List<MemberLink> findByMemberIdAndDisplayOrderBetween(Long memberId, Integer startOrder, Integer endOrder);
}
