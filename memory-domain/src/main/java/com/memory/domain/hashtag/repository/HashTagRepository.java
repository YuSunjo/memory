package com.memory.domain.hashtag.repository;

import com.memory.domain.hashtag.HashTag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HashTagRepository extends JpaRepository<HashTag, Long>, HashTagRepositoryCustom {
}