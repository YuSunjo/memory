package com.memory.service.map

import com.memory.domain.map.Map
import com.memory.domain.map.MapType
import com.memory.domain.map.repository.MapRepository
import com.memory.domain.member.repository.MemberRepository
import com.memory.dto.map.MapRequest
import com.memory.dto.map.response.MapResponse
import com.memory.dto.map.response.MapResponse.Companion.from
import com.memory.exception.customException.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class MapService(
    private val memberRepository: MemberRepository,
    private val mapRepository: MapRepository,
) {
    @Transactional
    fun createMap(createRequest: MapRequest.Create, memberId: Long?): MapResponse {
        val member = memberRepository.findMemberById(memberId)
            .orElseThrow{ NotFoundException("회원이 존재하지 않습니다.") }
        val savedMap = mapRepository.save<Map>(createRequest.toEntity(member))
        return from(savedMap)
    }

    @Transactional(readOnly = true)
    fun findMapById(mapId: Long): MapResponse {
        val map = mapRepository.findById(mapId)
            .orElseThrow{ NotFoundException("지도를 찾을 수 없습니다.") }
        return from(map)
    }

    @Transactional(readOnly = true)
    fun findMapsByType(mapType: MapType?): List<MapResponse?> {
        val maps: List<Map> = mapRepository.findByMapType(mapType)
        return maps.stream()
            .map { obj: Map -> from(obj) }
            .collect(Collectors.toList())
    }

    @Transactional(readOnly = true)
    fun findMapsByMemberAndType(memberId: Long?): List<MapResponse?> {
        val maps: List<Map> = mapRepository.findByMemberId(memberId)
        return maps.stream()
            .map { obj: Map -> from(obj) }
            .collect(Collectors.toList())
    }
}
