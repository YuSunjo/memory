package com.memory.domain.memory.repository;

import com.memory.domain.memory.Memory;
import com.memory.domain.member.Member;

import java.util.List;
import java.util.Optional;

public interface MemoryRepositoryCustom {
    List<Memory> findByMember(Member member);
    List<Memory> findByMember(Member member, Long lastMemoryId, int size);
    Optional<Memory> findMemoryByIdAndMemberId(Long memoryId, Long memberId);
}
