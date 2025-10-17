package com.memory.service.hashTag;

import com.memory.domain.hashtag.HashTag;
import com.memory.domain.hashtag.MemoryHashTag;
import com.memory.domain.hashtag.repository.HashTagRepository;
import com.memory.domain.hashtag.repository.MemoryHashTagRepository;
import com.memory.domain.memory.Memory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HashTagService {

    private final HashTagRepository hashTagRepository;
    private final MemoryHashTagRepository memoryHashTagRepository;

    @Transactional
    public List<HashTag> findOrCreateHashTags(List<String> hashTagNames) {
        if (hashTagNames == null || hashTagNames.isEmpty()) {
            return new ArrayList<>();
        }

        List<HashTag> hashTags = new ArrayList<>();
        
        for (String hashTagName : hashTagNames) {
            HashTag hashTag = hashTagRepository.findByName(hashTagName)
                    .orElseGet(() -> createHashTag(hashTagName));
            
            hashTag.incrementUseCount();
            hashTags.add(hashTag);
        }
        
        return hashTags;
    }

    @Transactional
    public void decrementUseCountForMemoryHashTags(Memory memory) {
        List<MemoryHashTag> memoryHashTags = memoryHashTagRepository.findByMemory(memory);
        for (MemoryHashTag memoryHashTag : memoryHashTags) {
            memoryHashTag.getHashTag().decrementUseCount();
        }
    }

    @Transactional(readOnly = true)
    public List<HashTag> searchHashTagsByName(String keyword, int limit) {
        return hashTagRepository.findHashTagsByNameContaining(keyword, limit);
    }

    @Transactional(readOnly = true)
    public List<HashTag> getPopularHashTags(int limit) {
        return hashTagRepository.findPopularHashTags(limit);
    }

    private HashTag createHashTag(String hashTagName) {
        HashTag hashTag = HashTag.create(hashTagName);
        return hashTagRepository.save(hashTag);
    }
}