package com.memory.domain.memory.repository

import com.memory.domain.memory.Memory
import com.memory.domain.member.Member
import com.memory.domain.memory.MemoryType
import java.util.Optional

interface MemoryRepositoryCustom {
    fun findByMemberAndMemoryType(member: Member?, relatedMemberIds: List<Long>?, memoryType: MemoryType?, size: Int): List<Memory>
    fun findByMemberAndMemoryType(member: Member?, relatedMemberIds: List<Long>?, memoryType: MemoryType?, lastMemoryId: Long?, size: Int): List<Memory>
    fun findMemoryByIdAndMemberId(memoryId: Long?, memberId: Long?): Optional<Memory>
    fun findByMemoryType(memoryType: MemoryType?, size: Int): List<Memory>
    fun findByMemoryType(memoryType: MemoryType?, lastMemoryId: Long?, size: Int): List<Memory>

    // 게임용 메서드들
    fun findMemoriesWithImagesByMember(member: Member?): List<Memory>
    fun findMemoriesWithImagesByMemoryType(memoryType: MemoryType?): List<Memory>
    fun findMemoryById(memoryId: Long?): Optional<Memory>
}
