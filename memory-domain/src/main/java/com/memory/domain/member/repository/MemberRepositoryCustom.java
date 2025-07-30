package com.memory.domain.member.repository;

import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;

import java.util.Optional;

public interface MemberRepositoryCustom {

    Optional<Member> findMemberById(Long memberId);

    Optional<Member> findMemberByEmailAndMemberType(String email, MemberType memberType);
}
