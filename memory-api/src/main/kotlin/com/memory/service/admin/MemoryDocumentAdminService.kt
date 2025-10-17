package com.memory.service.admin

import com.memory.document.memory.MemoryDocument
import com.memory.document.memory.MemoryDocumentRepository
import com.memory.document.memory.RelatedMember
import com.memory.document.memory.RelationshipInfo
import com.memory.domain.memory.Memory
import com.memory.domain.memory.repository.MemoryRepository
import com.memory.domain.relationship.Relationship
import com.memory.domain.relationship.RelationshipStatus
import com.memory.domain.relationship.repository.RelationshipRepository
import com.memory.dto.relationship.response.RelationshipListResponse
import com.memory.dto.relationship.response.RelationshipResponse
import com.memory.dto.search.MigrationResponse
import com.memory.exception.customException.NotFoundException
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class MemoryDocumentAdminService(
    private val memoryRepository: MemoryRepository,
    private val memoryDocumentRepository: MemoryDocumentRepository,
    private val relationshipRepository: RelationshipRepository,
) {
    private val log = LoggerFactory.getLogger(MemoryDocumentAdminService::class.java)

    @Transactional(readOnly = true)
    fun migrateAllMemories(): MigrationResponse {
        val startTime = System.currentTimeMillis()
        var processedCount: Long = 0
        var successCount: Long = 0
        var errorCount: Long = 0

        try {
            var pageNumber = 0
            var memoryPage: Page<Memory>?

            do {
                val pageable: Pageable = PageRequest.of(pageNumber, BATCH_SIZE)
                memoryPage = memoryRepository.findAll(pageable)

                for (memory in memoryPage.getContent()) {
                    processedCount++
                    try {
                        if (migrateIndividualMemory(memory)) {
                            successCount++
                        } else {
                            errorCount++
                        }
                    } catch (e: Exception) {
                        log.error(
                            "Failed to migrate memory ID: {}, Error: {}",
                            memory.id,
                            e.message
                        )
                        errorCount++
                    }
                }

                pageNumber++
            } while (memoryPage?.hasNext() == true)

            val elapsedTime = System.currentTimeMillis() - startTime

            val message = String.format(
                "마이그레이션 완료: 처리 %d건, 성공 %d건, 실패 %d건",
                processedCount, successCount, errorCount
            )

            return MigrationResponse.success(message, processedCount, successCount, errorCount, elapsedTime)
        } catch (e: Exception) {
            val elapsedTime = System.currentTimeMillis() - startTime
            log.error("Migration failed: {}", e.message)

            return MigrationResponse.failure(
                "마이그레이션 실패: " + e.message,
                processedCount, successCount, errorCount, elapsedTime
            )
        }
    }

    @Transactional(readOnly = true)
    fun migrateMemory(memoryId: Long): MigrationResponse {
        val startTime = System.currentTimeMillis()

        try {
            val memory = memoryRepository.findById(memoryId)
                .orElseThrow { NotFoundException("메모리를 찾을 수 없습니다: " + memoryId) }

            val success = migrateIndividualMemory(memory)
            val elapsedTime = System.currentTimeMillis() - startTime

            if (success) {
                return MigrationResponse.success("메모리 마이그레이션 성공", 1, 1, 0, elapsedTime)
            } else {
                return MigrationResponse.failure("메모리 마이그레이션 실패", 1, 0, 1, elapsedTime)
            }
        } catch (e: Exception) {
            val elapsedTime = System.currentTimeMillis() - startTime
            log.error("Failed to migrate memory ID: {}, Error: {}", memoryId, e.message)

            return MigrationResponse.failure(
                "마이그레이션 실패: " + e.message,
                1, 0, 1, elapsedTime
            )
        }
    }

    fun deleteAllDocuments(): MigrationResponse {
        val startTime = System.currentTimeMillis()

        try {
            memoryDocumentRepository.deleteAll()
            val elapsedTime = System.currentTimeMillis() - startTime

            return MigrationResponse.success("모든 문서 삭제 완료", 0, 0, 0, elapsedTime)
        } catch (e: Exception) {
            val elapsedTime = System.currentTimeMillis() - startTime
            log.error("Failed to delete all documents: {}", e.message)

            return MigrationResponse.failure(
                "문서 삭제 실패: " + e.message,
                0, 0, 0, elapsedTime
            )
        }
    }

    private fun migrateIndividualMemory(memory: Memory): Boolean {
        try {
            val relationships: List<Relationship> = relationshipRepository.findByMemberAndRelationshipStatus(
                memory.member,
                RelationshipStatus.ACCEPTED
            )
            val relationshipListResponse = RelationshipListResponse.fromEntities(relationships)

            val existingDocument = memoryDocumentRepository.findByMemoryId(memory.id!!)

            existingDocument.updateFromMemory(memory, convertToRelationshipInfo(relationshipListResponse))
            memoryDocumentRepository.save<MemoryDocument?>(existingDocument)

            return true
        } catch (e: Exception) {
            log.error("Failed to migrate memory ID: {}, Error: {}", memory.id, e.message)
            return false
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

    companion object {
        private const val BATCH_SIZE = 100
    }
}