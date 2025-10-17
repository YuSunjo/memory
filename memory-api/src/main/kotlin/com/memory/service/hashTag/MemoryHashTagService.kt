package com.memory.service.hashTag

import com.memory.domain.hashtag.HashTag
import com.memory.domain.hashtag.MemoryHashTag
import com.memory.domain.hashtag.repository.MemoryHashTagRepository
import com.memory.domain.memory.Memory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemoryHashTagService(
    private val memoryHashTagRepository: MemoryHashTagRepository,
    private val hashTagService: HashTagService,
) {
    @Transactional
    fun createMemoryHashTags(memory: Memory, hashTags: MutableList<HashTag?>?) {
        if (hashTags == null || hashTags.isEmpty()) {
            return
        }

        val memoryHashTags = hashTags.stream()
            .map { hashTag: HashTag? -> MemoryHashTag.create(memory, hashTag!!) }
            .toList()

        memoryHashTagRepository.saveAll(memoryHashTags)
        memory.addMemoryHashTags(memoryHashTags)
    }

    @Transactional
    fun updateMemoryHashTags(memory: Memory, newHashTags: MutableList<HashTag?>?) {
        // 기존 해시태그들의 사용 횟수 감소
        hashTagService.decrementUseCountForMemoryHashTags(memory)


        // 기존 관계 삭제
        memoryHashTagRepository.deleteByMemory(memory)
        memory.clearHashTags()

        // 새로운 해시태그들과 연결
        createMemoryHashTags(memory, newHashTags)
    }
}