package com.memory.domain.hashtag.repository

import com.memory.domain.hashtag.MemoryHashTag
import org.springframework.data.jpa.repository.JpaRepository

interface MemoryHashTagRepository : JpaRepository<MemoryHashTag, Long>, MemoryHashTagRepositoryCustom
