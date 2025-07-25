package com.memory.domain.hashtag.repository;

import com.memory.domain.hashtag.HashTag;

import java.util.List;
import java.util.Optional;

public interface HashTagRepositoryCustom {
    List<HashTag> findPopularHashTags(int limit);
    List<HashTag> findHashTagsByNameContaining(String keyword, int limit);
    Optional<HashTag> findByName(String name);
}