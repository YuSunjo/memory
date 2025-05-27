package com.memory.domain.member.repository;

import com.memory.domain.member.Member;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<Member> findMemberByEmail(String email);

    Optional<Member> findMemberById(Long memberId);
}
