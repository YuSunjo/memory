package com.memory.service.hashTag

import com.memory.domain.hashtag.HashTag
import com.memory.domain.hashtag.HashTag.Companion.create
import com.memory.domain.hashtag.MemoryHashTag
import com.memory.domain.hashtag.repository.HashTagRepository
import com.memory.domain.hashtag.repository.MemoryHashTagRepository
import com.memory.domain.memory.Memory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.function.Supplier

@Service
class HashTagService(
    private val hashTagRepository: HashTagRepository,
    private val memoryHashTagRepository: MemoryHashTagRepository,
) {
    @Transactional
    fun findOrCreateHashTags(hashTagNames: MutableList<String>?): MutableList<HashTag?> {
        if (hashTagNames == null || hashTagNames.isEmpty()) {
            return ArrayList()
        }

        val hashTags: MutableList<HashTag?> = ArrayList()

        for (hashTagName in hashTagNames) {
            val hashTag = hashTagRepository.findByName(hashTagName)
                .orElseGet(Supplier { createHashTag(hashTagName) })

            hashTag.incrementUseCount()
            hashTags.add(hashTag)
        }

        return hashTags
    }

    @Transactional
    fun decrementUseCountForMemoryHashTags(memory: Memory?) {
        val memoryHashTags: List<MemoryHashTag> = memoryHashTagRepository.findByMemory(memory)
        for (memoryHashTag in memoryHashTags) {
            memoryHashTag.hashTag!!.decrementUseCount()
        }
    }

    @Transactional(readOnly = true)
    fun searchHashTagsByName(keyword: String?, limit: Int): List<HashTag> {
        return hashTagRepository.findHashTagsByNameContaining(keyword, limit)
    }

    @Transactional(readOnly = true)
    fun getPopularHashTags(limit: Int): List<HashTag> {
        return hashTagRepository.findPopularHashTags(limit)
    }

    private fun createHashTag(hashTagName: String): HashTag {
        val hashTag = create(hashTagName)
        return hashTagRepository.save<HashTag>(hashTag)
    }
}