package com.memory.document.memory

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository
import org.springframework.stereotype.Repository

@Repository
interface MemoryDocumentRepository : ElasticsearchRepository<MemoryDocument, String>, MemoryDocumentRepositoryCustom {

    fun deleteByMemoryId(memoryId: Long)

    fun findByMemoryId(memoryId: Long): MemoryDocument
}
