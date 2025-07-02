package com.memory.service.memberlink;

import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.memberlink.MemberLink;
import com.memory.domain.memberlink.repository.MemberLinkRepository;
import com.memory.dto.memberlink.MemberLinkRequest;
import com.memory.dto.memberlink.response.MemberLinkResponse;
import com.memory.exception.customException.NotFoundException;
import com.memory.exception.customException.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberLinkService {

    private final MemberLinkRepository memberLinkRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public MemberLinkResponse createMemberLink(Long memberId, MemberLinkRequest.Create request) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        Integer nextDisplayOrder = memberLinkRepository.findMaxDisplayOrderByMemberId(memberId) + 1;

        MemberLink memberLink = request.toEntity(member, nextDisplayOrder);
        MemberLink savedMemberLink = memberLinkRepository.save(memberLink);
        member.addMemberLink(savedMemberLink);

        return MemberLinkResponse.from(savedMemberLink);
    }

    @Transactional
    public MemberLinkResponse updateMemberLink(Long memberId, Long linkId, MemberLinkRequest.Update request) {
        MemberLink memberLink = memberLinkRepository.findByIdAndMemberId(linkId, memberId)
                .orElseThrow(() -> new NotFoundException("링크를 찾을 수 없거나 수정 권한이 없습니다."));

        memberLink.update(
                request.getTitle(),
                request.getUrl(),
                request.getDescription(),
                request.getDisplayOrder(),
                request.getIsActive(),
                request.getIsVisible(),
                request.getIconUrl()
        );

        return MemberLinkResponse.from(memberLink);
    }

    @Transactional
    public MemberLinkResponse updateMemberLinkOrder(Long memberId, Long linkId, MemberLinkRequest.UpdateOrder request) {
        MemberLink memberLink = memberLinkRepository.findByIdAndMemberId(linkId, memberId)
                .orElseThrow(() -> new NotFoundException("링크를 찾을 수 없거나 수정 권한이 없습니다."));

        Integer newDisplayOrder = request.getDisplayOrder();
        Long totalCount = memberLinkRepository.countByMemberId(memberId);
        
        if (newDisplayOrder < 1 || newDisplayOrder > totalCount) {
            throw new IllegalArgumentException("유효하지 않은 순서입니다. (1 ~ " + totalCount + ")");
        }

        if (memberLink.isSameOrder(newDisplayOrder)) {
            return MemberLinkResponse.from(memberLink);
        }

        // 순서 재배치 로직
        Integer currentOrder = memberLink.getDisplayOrder();
        if (currentOrder < newDisplayOrder) {
            // 뒤로 이동: 현재 순서보다 크고 새 순서보다 작거나 같은 항목들을 앞으로 당김
            List<MemberLink> linksToUpdate = memberLinkRepository
                    .findByMemberIdAndDisplayOrderBetween(memberId, currentOrder + 1, newDisplayOrder);
            for (MemberLink link : linksToUpdate) {
                link.updateDisplayOrder(link.getDisplayOrder() - 1);
            }
        } else {
            // 앞으로 이동: 새 순서보다 크거나 같고 현재 순서보다 작은 항목들을 뒤로 밀어냄
            List<MemberLink> linksToUpdate = memberLinkRepository
                    .findByMemberIdAndDisplayOrderBetween(memberId, newDisplayOrder, currentOrder - 1);
            for (MemberLink link : linksToUpdate) {
                link.updateDisplayOrder(link.getDisplayOrder() + 1);
            }
        }

        memberLink.updateDisplayOrder(newDisplayOrder);

        return MemberLinkResponse.from(memberLink);
    }

    @Transactional(readOnly = true)
    public List<MemberLinkResponse> getMemberLinks(Long memberId) {
        List<MemberLink> memberLinks = memberLinkRepository.findActiveByMemberIdOrderByDisplayOrder(memberId);
        
        return memberLinks.stream()
                .map(MemberLinkResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<MemberLinkResponse> getPublicMemberLinks(Long memberId) {
        memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("회원을 찾을 수 없습니다."));

        List<MemberLink> publicLinks = memberLinkRepository.findPublicByMemberIdOrderByDisplayOrder(memberId);

        return publicLinks.stream()
                .map(MemberLinkResponse::forPublic)
                .toList();
    }

    @Transactional
    public void deleteMemberLink(Long memberId, Long linkId) {
        MemberLink memberLink = memberLinkRepository.findByIdAndMemberId(linkId, memberId)
                .orElseThrow(() -> new NotFoundException("링크를 찾을 수 없거나 삭제 권한이 없습니다."));

        memberLink.updateDelete();
    }

    @Transactional
    public MemberLinkResponse incrementClickCount(Long linkId) {
        MemberLink memberLink = memberLinkRepository.findById(linkId)
                .orElseThrow(() -> new NotFoundException("링크를 찾을 수 없습니다."));

        if (memberLink.isDeleted()) {
            throw new NotFoundException("삭제된 링크입니다.");
        }

        if (!memberLink.isAccessible()) {
            throw new ValidationException("접근할 수 없는 링크입니다.");
        }

        memberLink.incrementClickCount();

        return MemberLinkResponse.forPublic(memberLink);
    }
}
