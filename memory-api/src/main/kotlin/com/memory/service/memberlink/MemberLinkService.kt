package com.memory.service.memberlink

import com.memory.domain.member.repository.MemberRepository
import com.memory.domain.memberlink.MemberLink
import com.memory.domain.memberlink.repository.MemberLinkRepository
import com.memory.dto.member.response.MemberResponse.Companion.from
import com.memory.dto.memberlink.MemberLinkRequest
import com.memory.dto.memberlink.response.MemberLinkResponse
import com.memory.dto.memberlink.response.MemberLinkResponse.Companion.forPublic
import com.memory.dto.memberlink.response.MemberLinkResponse.Companion.from
import com.memory.dto.memberlink.response.MemberPublicLinkResponse
import com.memory.dto.memberlink.response.MemberPublicLinkResponse.Companion.of
import com.memory.exception.customException.NotFoundException
import com.memory.exception.customException.ValidationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemberLinkService(
    private val memberLinkRepository: MemberLinkRepository,
    private val memberRepository: MemberRepository,
) {
    @Transactional
    fun createMemberLink(memberId: Long?, request: MemberLinkRequest.Create): MemberLinkResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val nextDisplayOrder = memberLinkRepository.findMaxDisplayOrderByMemberId(memberId)?.plus(1) ?: 1

        val memberLink = request.toEntity(member, nextDisplayOrder)
        val savedMemberLink = memberLinkRepository.save<MemberLink>(memberLink)
        member.addMemberLink(savedMemberLink)

        return from(savedMemberLink)
    }

    @Transactional
    fun updateMemberLink(memberId: Long?, linkId: Long?, request: MemberLinkRequest.Update): MemberLinkResponse {
        val memberLink = memberLinkRepository.findByIdAndMemberId(linkId, memberId)
            .orElseThrow { NotFoundException("링크를 찾을 수 없거나 수정 권한이 없습니다.") }

        memberLink.update(
            request.title,
            request.url,
            request.description,
            request.displayOrder,
            request.isActive,
            request.isVisible,
            request.iconUrl
        )

        return from(memberLink)
    }

    @Transactional
    fun updateMemberLinkOrder(
        memberId: Long?,
        linkId: Long?,
        request: MemberLinkRequest.UpdateOrder
    ): MemberLinkResponse {
        val memberLink = memberLinkRepository.findByIdAndMemberId(linkId, memberId)
            .orElseThrow { NotFoundException("링크를 찾을 수 없거나 수정 권한이 없습니다.") }

        val newDisplayOrder = request.displayOrder
        val totalCount = memberLinkRepository.countByMemberId(memberId) ?: 0L

        if (newDisplayOrder < 1 || newDisplayOrder > totalCount) {
            throw ValidationException("유효하지 않은 순서입니다. (1 ~ $totalCount)")
        }

        if (memberLink.isSameOrder(newDisplayOrder)) {
            return from(memberLink)
        }

        // 순서 재배치 로직
        val currentOrder = memberLink.displayOrder
        if (currentOrder < newDisplayOrder) {
            // 뒤로 이동: 현재 순서보다 크고 새 순서보다 작거나 같은 항목들을 앞으로 당김
            val linksToUpdate: List<MemberLink> = memberLinkRepository
                .findByMemberIdAndDisplayOrderBetween(memberId, currentOrder + 1, newDisplayOrder)
            for (link in linksToUpdate) {
                link.updateDisplayOrder(link.displayOrder - 1)
            }
        } else {
            // 앞으로 이동: 새 순서보다 크거나 같고 현재 순서보다 작은 항목들을 뒤로 밀어냄
            val linksToUpdate: List<MemberLink> = memberLinkRepository
                .findByMemberIdAndDisplayOrderBetween(memberId, newDisplayOrder, currentOrder - 1)
            for (link in linksToUpdate) {
                link.updateDisplayOrder(link.displayOrder + 1)
            }
        }

        memberLink.updateDisplayOrder(newDisplayOrder)

        return from(memberLink)
    }

    @Transactional(readOnly = true)
    fun getMemberLinks(memberId: Long?): List<MemberLinkResponse> {
        val memberLinks: List<MemberLink> =
            memberLinkRepository.findActiveByMemberIdOrderByDisplayOrder(memberId)

        return memberLinks.stream()
            .map { obj: MemberLink -> from(obj) }
            .toList()
    }

    @Transactional(readOnly = true)
    fun getPublicMemberLinks(memberId: Long?): MemberPublicLinkResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow { NotFoundException("회원을 찾을 수 없습니다.") }

        val publicLinks: List<MemberLink> =
            memberLinkRepository.findPublicByMemberIdOrderByDisplayOrder(memberId)

        val linkResponses = publicLinks.stream()
            .map { obj: MemberLink -> forPublic(obj) }
            .toList()
        return of(linkResponses, from(member))
    }

    @Transactional
    fun deleteMemberLink(memberId: Long?, linkId: Long?) {
        val memberLink = memberLinkRepository.findByIdAndMemberId(linkId, memberId)
            .orElseThrow { NotFoundException("링크를 찾을 수 없거나 삭제 권한이 없습니다.") }

        memberLink.updateDelete()
    }

    @Transactional
    fun incrementClickCount(linkId: Long): MemberLinkResponse {
        val memberLink = memberLinkRepository.findById(linkId)
            .orElseThrow { NotFoundException("링크를 찾을 수 없습니다.") }

        if (memberLink.isDeleted()) {
            throw NotFoundException("삭제된 링크입니다.")
        }

        if (!memberLink.isAccessible()) {
            throw ValidationException("접근할 수 없는 링크입니다.")
        }

        memberLink.incrementClickCount()

        return forPublic(memberLink)
    }
}
