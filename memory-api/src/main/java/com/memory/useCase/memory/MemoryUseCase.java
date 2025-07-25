package com.memory.useCase.memory;

import com.memory.domain.hashtag.HashTag;
import com.memory.domain.memory.Memory;
import com.memory.dto.memory.MemoryRequest;
import com.memory.dto.memory.response.MemoryResponse;
import com.memory.service.hashTag.HashTagService;
import com.memory.service.hashTag.MemoryHashTagService;
import com.memory.service.memory.MemoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryUseCase {

    private final MemoryService memoryService;
    private final HashTagService hashTagService;
    private final MemoryHashTagService memoryHashTagService;

    @Transactional
    public MemoryResponse createMemoryWithHashTags(Long memberId, MemoryRequest.Create createRequest) {
        // 1. 메모리 생성
        MemoryResponse memoryResponse = memoryService.createMemory(memberId, createRequest);
        
        // 2. 해시태그 처리
        if (createRequest.getHashTagList() != null && !createRequest.getHashTagList().isEmpty()) {
            List<HashTag> hashTags = hashTagService.findOrCreateHashTags(createRequest.getHashTagList());
            
            Memory memory = memoryService.findMemoryEntityById(memberId, memoryResponse.id());
            memoryHashTagService.createMemoryHashTags(memory, hashTags);
        }
        
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
        
        return memoryResponse;
    }

}
