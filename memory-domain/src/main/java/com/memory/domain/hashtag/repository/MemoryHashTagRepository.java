package com.memory.domain.hashtag.repository;

import com.memory.domain.hashtag.MemoryHashTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryHashTagRepository extends JpaRepository<MemoryHashTag, Long>, MemoryHashTagRepositoryCustom {
}