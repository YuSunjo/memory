package com.memory.domain.hashtag.repository

import com.memory.domain.hashtag.HashTag
import java.util.Optional

interface HashTagRepositoryCustom {
    fun findPopularHashTags(limit: Int): List<HashTag>
    fun findHashTagsByNameContaining(keyword: String?, limit: Int): List<HashTag>
    fun findByName(name: String?): Optional<HashTag>
}
