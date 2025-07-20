package com.memory.domain.memory.repository;

import com.memory.domain.memory.Memory;
import com.memory.domain.member.Member;
import com.memory.domain.memory.MemoryType;

import java.util.List;
import java.util.Optional;

public interface MemoryRepositoryCustom {
    List<Memory> findByMemberAndMemoryType(Member member, MemoryType memoryType, int size);
    List<Memory> findByMemberAndMemoryType(Member member, MemoryType memoryType, Long lastMemoryId, int size);
    Optional<Memory> findMemoryByIdAndMemberId(Long memoryId, Long memberId);
    List<Memory> findByMemoryType(MemoryType memoryType, int size);
    List<Memory> findByMemoryType(MemoryType memoryType, Long lastMemoryId, int size);
    
    // 게임용 메서드들
    List<Memory> findMemoriesWithImagesByMember(Member member);
    List<Memory> findMemoriesWithImagesByMemoryType(MemoryType memoryType);

    Optional<Memory> findMemoryById(Long memoryId);
}
