package com.memory.domain.memory.repository

import com.memory.domain.memory.Memory
import org.springframework.data.jpa.repository.JpaRepository

interface MemoryRepository : JpaRepository<Memory, Long>, MemoryRepositoryCustom
