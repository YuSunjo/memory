package com.memory.domain.hashtag.repository

import com.memory.domain.hashtag.MemoryHashTag
import com.memory.domain.memory.Memory

interface MemoryHashTagRepositoryCustom {
    fun findByMemory(memory: Memory?): List<MemoryHashTag>
    fun deleteByMemory(memory: Memory?)
}
