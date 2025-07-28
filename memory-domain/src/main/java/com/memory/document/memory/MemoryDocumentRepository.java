package com.memory.document.memory;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemoryDocumentRepository extends ElasticsearchRepository<MemoryDocument, String>, MemoryDocumentRepositoryCustom {
}