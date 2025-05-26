package com.memory.service.member;

import com.memory.domain.member.Member;
import com.memory.domain.member.repository.MemberRepository;
import com.memory.dto.member.MemberRequest;
import com.memory.dto.member.MemberResponse;
import com.memory.exception.customException.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    public MemberResponse signup(MemberRequest.Signup signupRequestDto) {
        memberRepository.findMemberByEmail(signupRequestDto.getEmail())
                .ifPresent(member -> {
                    throw new ConflictException("이미 존재하는 이메일입니다.");
                });

        Member member = memberRepository.save(signupRequestDto.toEntity());
        return MemberResponse.from(member);
    }
}
