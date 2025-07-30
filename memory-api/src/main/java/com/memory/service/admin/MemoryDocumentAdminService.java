package com.memory.service.admin;

import com.memory.document.memory.MemoryDocument;
import com.memory.document.memory.MemoryDocumentRepository;
import com.memory.domain.memory.Memory;
import com.memory.domain.memory.repository.MemoryRepository;
import com.memory.domain.relationship.Relationship;
import com.memory.domain.relationship.RelationshipStatus;
import com.memory.domain.relationship.repository.RelationshipRepository;
import com.memory.dto.relationship.response.RelationshipListResponse;
import com.memory.dto.search.MigrationResponse;
import com.memory.exception.customException.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemoryDocumentAdminService {

    private final MemoryRepository memoryRepository;
    private final MemoryDocumentRepository memoryDocumentRepository;
    private final RelationshipRepository relationshipRepository;

    private static final int BATCH_SIZE = 100;

    @Transactional(readOnly = true)
    public MigrationResponse migrateAllMemories() {
        long startTime = System.currentTimeMillis();
        long processedCount = 0;
        long successCount = 0;
        long errorCount = 0;

        try {
            int pageNumber = 0;
            Page<Memory> memoryPage;

            do {
                Pageable pageable = PageRequest.of(pageNumber, BATCH_SIZE);
                memoryPage = memoryRepository.findAll(pageable);

                for (Memory memory : memoryPage.getContent()) {
                    processedCount++;
                    try {
                        if (migrateIndividualMemory(memory)) {
                            successCount++;
                        } else {
                            errorCount++;
                        }
                    } catch (Exception e) {
                        log.error("Failed to migrate memory ID: {}, Error: {}", memory.getId(), e.getMessage());
                        errorCount++;
                    }
                }

                pageNumber++;
            } while (memoryPage.hasNext());

            long elapsedTime = System.currentTimeMillis() - startTime;
            
            String message = String.format("마이그레이션 완료: 처리 %d건, 성공 %d건, 실패 %d건", 
                                          processedCount, successCount, errorCount);

            return MigrationResponse.success(message, processedCount, successCount, errorCount, elapsedTime);

        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.error("Migration failed: {}", e.getMessage());
            
            return MigrationResponse.failure("마이그레이션 실패: " + e.getMessage(), 
                                            processedCount, successCount, errorCount, elapsedTime);
        }
    }

    @Transactional(readOnly = true)
    public MigrationResponse migrateMemory(Long memoryId) {
        long startTime = System.currentTimeMillis();

        try {
            Memory memory = memoryRepository.findById(memoryId)
                    .orElseThrow(() -> new NotFoundException("메모리를 찾을 수 없습니다: " + memoryId));

            boolean success = migrateIndividualMemory(memory);
            long elapsedTime = System.currentTimeMillis() - startTime;

            if (success) {
                return MigrationResponse.success("메모리 마이그레이션 성공", 1, 1, 0, elapsedTime);
            } else {
                return MigrationResponse.failure("메모리 마이그레이션 실패", 1, 0, 1, elapsedTime);
            }

        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.error("Failed to migrate memory ID: {}, Error: {}", memoryId, e.getMessage());
            
            return MigrationResponse.failure("마이그레이션 실패: " + e.getMessage(), 
                                            1, 0, 1, elapsedTime);
        }
    }

    public MigrationResponse deleteAllDocuments() {
        long startTime = System.currentTimeMillis();

        try {
            memoryDocumentRepository.deleteAll();
            long elapsedTime = System.currentTimeMillis() - startTime;

            return MigrationResponse.success("모든 문서 삭제 완료", 0, 0, 0, elapsedTime);

        } catch (Exception e) {
            long elapsedTime = System.currentTimeMillis() - startTime;
            log.error("Failed to delete all documents: {}", e.getMessage());
            
            return MigrationResponse.failure("문서 삭제 실패: " + e.getMessage(), 
                                            0, 0, 0, elapsedTime);
        }
    }

    private boolean migrateIndividualMemory(Memory memory) {
        try {
            List<Relationship> relationships = relationshipRepository.findByMemberAndRelationshipStatus(memory.getMember(),
                    RelationshipStatus.ACCEPTED);
            RelationshipListResponse relationshipListResponse = RelationshipListResponse.fromEntities(relationships);
            
            MemoryDocument existingDocument = memoryDocumentRepository.findByMemoryId(memory.getId());
            
            if (existingDocument != null) {
                existingDocument.updateFromMemory(memory, relationshipListResponse);
                memoryDocumentRepository.save(existingDocument);
            } else {
                MemoryDocument newDocument = MemoryDocument.from(memory, relationshipListResponse);
                memoryDocumentRepository.save(newDocument);
            }
            
            return true;
            
        } catch (Exception e) {
            log.error("Failed to migrate memory ID: {}, Error: {}", memory.getId(), e.getMessage());
            return false;
        }
    }
}