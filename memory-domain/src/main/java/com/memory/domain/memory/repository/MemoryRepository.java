package com.memory.domain.memory.repository;

import com.memory.domain.memory.Memory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemoryRepository extends JpaRepository<Memory, Long>, MemoryRepositoryCustom {
}