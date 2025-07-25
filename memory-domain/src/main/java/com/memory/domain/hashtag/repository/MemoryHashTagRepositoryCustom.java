package com.memory.domain.hashtag.repository;

import com.memory.domain.hashtag.MemoryHashTag;
import com.memory.domain.memory.Memory;

import java.util.List;

public interface MemoryHashTagRepositoryCustom {
    List<MemoryHashTag> findByMemory(Memory memory);
    void deleteByMemory(Memory memory);
}