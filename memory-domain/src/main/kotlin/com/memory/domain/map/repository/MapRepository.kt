package com.memory.domain.map.repository

import com.memory.domain.map.Map
import org.springframework.data.jpa.repository.JpaRepository

interface MapRepository : JpaRepository<Map, Long>, MapRepositoryCustom
