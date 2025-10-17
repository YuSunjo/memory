package com.memory.service.document

import com.memory.document.memory.MemoryDocument
import com.memory.document.memory.MemoryDocument.Companion.from
import com.memory.document.memory.MemoryDocumentRepository
import com.memory.document.memory.RelatedMember
import com.memory.document.memory.RelationshipInfo
import com.memory.domain.memory.Memory
import com.memory.dto.relationship.response.RelationshipListResponse
import com.memory.dto.relationship.response.RelationshipResponse
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class MemoryDocumentService(
    private val memoryDocumentRepository: MemoryDocumentRepository,
) {
    private val log = LoggerFactory.getLogger(MemoryDocumentService::class.java)

    fun indexMemory(memory: Memory, relationships: RelationshipListResponse?) {
        try {
            val document = from(memory, convertToRelationshipInfo(relationships))
            memoryDocumentRepository.save<MemoryDocument?>(document)
        } catch (e: Exception) {
            log.error("Failed to index memory to Elasticsearch. memoryId: {}", memory.id, e)
        }
    }

    fun updateMemoryIndex(memory: Memory, relationships: RelationshipListResponse?) {
        try {
            // memoryId로 기존 문서 조회 후 업데이트
            val existingDoc = memoryDocumentRepository.findByMemoryId(memory.id!!)
            existingDoc.updateFromMemory(memory, convertToRelationshipInfo(relationships))
            memoryDocumentRepository.save<MemoryDocument?>(existingDoc)
        } catch (e: Exception) {
            log.error(
                "Failed to update memory index in Elasticsearch. memoryId: {}",
                memory.id,
                e
            )
        }
    }

    fun deleteMemoryIndex(memoryId: Long) {
        try {
            // memoryId 필드로 검색해서 해당 문서들 삭제
            memoryDocumentRepository.deleteByMemoryId(memoryId)
        } catch (e: Exception) {
            log.error(
                "Failed to delete memory index from Elasticsearch. memoryId: {}",
                memoryId,
                e
            )
        }
    }

    private fun convertToRelationshipInfo(relationshipListResponse: RelationshipListResponse?): RelationshipInfo {
        if (relationshipListResponse == null || relationshipListResponse.relationships == null) {
            return RelationshipInfo(null)
        }

        val relationships = relationshipListResponse.relationships.stream()
            .map { rel: RelationshipResponse? ->
                rel?.relatedMember?.let { member ->
                    RelatedMember(
                        member.id,
                        member.name,
                        member.nickname,
                        member.email,
                        member.profile?.fileUrl
                    )
                }
            }
            .filter { it != null }
            .map { it!! }
            .toList()

        return RelationshipInfo(relationships)
    }
}