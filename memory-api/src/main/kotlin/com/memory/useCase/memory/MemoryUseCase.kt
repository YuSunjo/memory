package com.memory.useCase.memory

import com.memory.domain.relationship.RelationshipStatus
import com.memory.dto.memory.MemoryRequest
import com.memory.dto.memory.response.MemoryResponse
import com.memory.service.document.MemoryDocumentService
import com.memory.service.hashTag.HashTagService
import com.memory.service.hashTag.MemoryHashTagService
import com.memory.service.memory.MemoryService
import com.memory.service.relationship.RelationshipService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemoryUseCase(
    private val memoryService: MemoryService,
    private val relationshipService: RelationshipService,
    private val hashTagService: HashTagService,
    private val memoryHashTagService: MemoryHashTagService,
    private val memoryDocumentService: MemoryDocumentService,
) {
    @Transactional
    fun createMemoryWithHashTags(memberId: Long?, createRequest: MemoryRequest.Create): MemoryResponse {
        // 1. 메모리 생성
        val memoryResponse = memoryService.createMemory(memberId, createRequest)

        val memory = memoryService.findMemoryEntityById(memberId, memoryResponse.id)

        // 2. 해시태그 처리
        if (!createRequest.hashTagList.isEmpty()) {
            val hashTags = hashTagService.findOrCreateHashTags(createRequest.hashTagList as MutableList<String>?)

            memoryHashTagService.createMemoryHashTags(memory, hashTags)
        }
        val relationships = relationshipService.getRelationshipsByStatus(memberId, RelationshipStatus.ACCEPTED)

        // 3. Elasticsearch 인덱싱 저장
        memoryDocumentService.indexMemory(memory, relationships)

        return memoryResponse
    }

    @Transactional
    fun updateMemoryWithHashTags(
        memberId: Long?,
        memoryId: Long?,
        updateRequest: MemoryRequest.Update
    ): MemoryResponse {
        // 1. 메모리 업데이트
        val memoryResponse = memoryService.updateMemory(memberId, memoryId, updateRequest)

        // 2. 해시태그 처리
        val memory = memoryService.findMemoryEntityById(memberId, memoryId)
        val newHashTags = hashTagService.findOrCreateHashTags(updateRequest.hashTagList as MutableList<String>?)
        memoryHashTagService.updateMemoryHashTags(memory, newHashTags)


        // 3. Elasticsearch 인덱스 업데이트
        val relationships = relationshipService.getRelationshipsByStatus(memberId, RelationshipStatus.ACCEPTED)
        memoryDocumentService.updateMemoryIndex(memory, relationships)

        return memoryResponse
    }

    @Transactional
    fun deleteMemory(memberId: Long?, memoryId: Long) {
        memoryService.deleteMemory(memberId, memoryId)

        // Elasticsearch 인덱스 삭제
        memoryDocumentService.deleteMemoryIndex(memoryId)
    }
}
