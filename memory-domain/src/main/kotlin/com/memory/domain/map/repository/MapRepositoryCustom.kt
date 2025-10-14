package com.memory.domain.map.repository

import com.memory.domain.map.Map
import com.memory.domain.map.MapType

interface MapRepositoryCustom {
    fun findByMapType(mapType: MapType?): List<Map>
    fun findByMemberId(memberId: Long?): List<Map>
}
