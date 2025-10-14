package com.memory.domain.memberlink.repository

import com.memory.domain.memberlink.MemberLink
import java.util.Optional

interface MemberLinkRepositoryCustom {

    fun findActiveByMemberIdOrderByDisplayOrder(memberId: Long?): List<MemberLink>

    fun findPublicByMemberIdOrderByDisplayOrder(memberId: Long?): List<MemberLink>

    fun countByMemberId(memberId: Long?): Long?

    fun findMaxDisplayOrderByMemberId(memberId: Long?): Int?

    fun findByIdAndMemberId(linkId: Long?, memberId: Long?): Optional<MemberLink>

    fun findByMemberIdAndDisplayOrderBetween(memberId: Long?, startOrder: Int?, endOrder: Int?): List<MemberLink>
}
