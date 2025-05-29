package com.memory.service.relationship;

import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.domain.relationship.Relationship;
import com.memory.domain.relationship.repository.RelationshipRepository;
import com.memory.dto.relationship.RelationshipRequest;
import com.memory.dto.relationship.response.RelationshipResponse;
import com.memory.exception.customException.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RelationshipService {

    private final RelationshipRepository relationshipRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public RelationshipResponse createRelationshipRequest(Long memberId, RelationshipRequest.Create createRequestDto) {
        Member member = memberRepository.findMemberById(memberId)
                .orElseThrow(() -> new NotFoundException("요청한 회원을 찾을 수 없습니다."));

        Member relatedMember = memberRepository.findMemberById(createRequestDto.getRelatedMemberId())
                .orElseThrow(() -> new NotFoundException("대상 회원을 찾을 수 없습니다."));

        Relationship relationship = Relationship.createPendingRelationship(member, relatedMember);
        relationshipRepository.save(relationship);

        return RelationshipResponse.from(relationship);
    }
}
