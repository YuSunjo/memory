package com.memory.domain.hashtag.repository

import com.memory.domain.hashtag.HashTag
import org.springframework.data.jpa.repository.JpaRepository

interface HashTagRepository : JpaRepository<HashTag, Long>, HashTagRepositoryCustom
