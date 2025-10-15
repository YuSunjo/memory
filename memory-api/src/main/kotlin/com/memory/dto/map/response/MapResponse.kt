package com.memory.dto.map.response

import com.memory.domain.map.Map
import com.memory.domain.map.MapType
import com.memory.dto.member.response.MemberResponse

data class MapResponse(
    val id: Long?,
    val name: String?,
    val description: String?,
    val address: String?,
    val latitude: String?,
    val longitude: String?,
    val mapType: MapType?,
    val member: MemberResponse?
) {
    companion object {
        @JvmStatic
        fun from(map: Map): MapResponse {
            return MapResponse(
                map.id,
                map.name,
                map.description,
                map.address,
                map.latitude,
                map.longitude,
                map.mapType,
                MemberResponse.from(map.member)
            )
        }
    }
}