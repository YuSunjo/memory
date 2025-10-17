package com.memory.service.hashTag;

import com.memory.domain.hashtag.HashTag;
import com.memory.domain.hashtag.MemoryHashTag;
import com.memory.domain.hashtag.repository.MemoryHashTagRepository;
import com.memory.domain.memory.Memory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MemoryHashTagService {

    private final MemoryHashTagRepository memoryHashTagRepository;
    private final HashTagService hashTagService;

    @Transactional
    public void createMemoryHashTags(Memory memory, List<HashTag> hashTags) {
        if (hashTags == null || hashTags.isEmpty()) {
            return;
        }

        List<MemoryHashTag> memoryHashTags = hashTags.stream()
                .map(hashTag -> MemoryHashTag.create(memory, hashTag))
                .toList();

        memoryHashTagRepository.saveAll(memoryHashTags);
        memory.addMemoryHashTags(memoryHashTags);
    }

    @Transactional
    public void updateMemoryHashTags(Memory memory, List<HashTag> newHashTags) {
        // 기존 해시태그들의 사용 횟수 감소
        hashTagService.decrementUseCountForMemoryHashTags(memory);
        
        // 기존 관계 삭제
        memoryHashTagRepository.deleteByMemory(memory);
        memory.clearHashTags();

        // 새로운 해시태그들과 연결
        createMemoryHashTags(memory, newHashTags);
    }
}