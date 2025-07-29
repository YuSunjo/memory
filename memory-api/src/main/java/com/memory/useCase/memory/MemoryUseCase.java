package com.memory.useCase.memory;

import com.memory.domain.hashtag.HashTag;
import com.memory.domain.memory.Memory;
import com.memory.dto.memory.MemoryRequest;
import com.memory.dto.memory.response.MemoryResponse;
import com.memory.dto.relationship.response.RelationshipListResponse;
import com.memory.service.document.MemoryDocumentService;
import com.memory.service.hashTag.HashTagService;
import com.memory.service.hashTag.MemoryHashTagService;
import com.memory.service.memory.MemoryService;
import com.memory.service.relationship.RelationshipService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryUseCase {

    private final MemoryService memoryService;
    private final RelationshipService relationshipService;
    private final HashTagService hashTagService;
    private final MemoryHashTagService memoryHashTagService;
    private final MemoryDocumentService memoryDocumentService;

    @Transactional
    public MemoryResponse createMemoryWithHashTags(Long memberId, MemoryRequest.Create createRequest) {
        // 1. 메모리 생성
        MemoryResponse memoryResponse = memoryService.createMemory(memberId, createRequest);

        Memory memory = memoryService.findMemoryEntityById(memberId, memoryResponse.id());

        // 2. 해시태그 처리
        if (createRequest.getHashTagList() != null && !createRequest.getHashTagList().isEmpty()) {
            List<HashTag> hashTags = hashTagService.findOrCreateHashTags(createRequest.getHashTagList());
            
            memoryHashTagService.createMemoryHashTags(memory, hashTags);
        }
        RelationshipListResponse relationships = relationshipService.getRelationships(memberId);

        // 3. Elasticsearch 인덱싱 저장
        memoryDocumentService.indexMemory(memory, relationships);
        
        return memoryResponse;
    }

    @Transactional
    public MemoryResponse updateMemoryWithHashTags(Long memberId, Long memoryId, MemoryRequest.Update updateRequest) {
        // 1. 메모리 업데이트
        MemoryResponse memoryResponse = memoryService.updateMemory(memberId, memoryId, updateRequest);
        
        // 2. 해시태그 처리
        Memory memory = memoryService.findMemoryEntityById(memberId, memoryId);
        List<HashTag> newHashTags = hashTagService.findOrCreateHashTags(updateRequest.getHashTagList());
        memoryHashTagService.updateMemoryHashTags(memory, newHashTags);
        
        // 3. Elasticsearch 인덱스 업데이트
        RelationshipListResponse relationships = relationshipService.getRelationships(memberId);
        memoryDocumentService.updateMemoryIndex(memory, relationships);
        
        return memoryResponse;
    }

    @Transactional
    public void deleteMemory(Long memberId, Long memoryId) {
        memoryService.deleteMemory(memberId, memoryId);

        // Elasticsearch 인덱스 삭제
        memoryDocumentService.deleteMemoryIndex(memoryId);
    }
}
