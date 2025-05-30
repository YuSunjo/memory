package com.memory.domain.map.repository;

import com.memory.domain.map.Map;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MapRepository extends JpaRepository<Map, Long>, MapRepositoryCustom {
}
