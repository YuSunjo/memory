package com.memory.service.document;

import com.memory.document.memory.MemoryDocument;
import com.memory.document.memory.MemoryDocumentRepository;
import com.memory.domain.memory.Memory;
import com.memory.dto.relationship.response.RelationshipListResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryDocumentService {

    private final MemoryDocumentRepository memoryDocumentRepository;

    public void indexMemory(Memory memory, RelationshipListResponse relationships) {
        try {
            MemoryDocument document = MemoryDocument.from(memory, relationships);
            memoryDocumentRepository.save(document);
        } catch (Exception e) {
            log.error("Failed to index memory to Elasticsearch. memoryId: {}", memory.getId(), e);
        }
    }

    public void updateMemoryIndex(Memory memory, RelationshipListResponse relationships) {
        try {
            // memoryId로 기존 문서 조회 후 업데이트
            MemoryDocument existingDoc = memoryDocumentRepository.findByMemoryId(memory.getId());
            if (existingDoc != null) {
                existingDoc.updateFromMemory(memory, relationships);
                memoryDocumentRepository.save(existingDoc);
            } else {
                // 문서가 없으면 새로 생성
                MemoryDocument newDocument = MemoryDocument.from(memory, relationships);
                memoryDocumentRepository.save(newDocument);
            }
        } catch (Exception e) {
            log.error("Failed to update memory index in Elasticsearch. memoryId: {}", memory.getId(), e);
        }
    }

    public void deleteMemoryIndex(Long memoryId) {
        try {
            // memoryId 필드로 검색해서 해당 문서들 삭제
            memoryDocumentRepository.deleteByMemoryId(memoryId);
        } catch (Exception e) {
            log.error("Failed to delete memory index from Elasticsearch. memoryId: {}", memoryId, e);
        }
    }
}