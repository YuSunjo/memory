package com.memory.domain.map.repository;

import com.memory.domain.map.Map;
import com.memory.domain.map.MapType;

import java.util.List;

public interface MapRepositoryCustom {
    List<Map> findByMapType(MapType mapType);
}