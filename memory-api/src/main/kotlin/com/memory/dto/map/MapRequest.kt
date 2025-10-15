package com.memory.dto.map

import com.memory.domain.map.Map
import com.memory.domain.map.MapType
import com.memory.domain.member.Member
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

class MapRequest {

    data class Create(

        @field:NotBlank(message = "이름은 필수 입력값입니다.")
        val name: String,

        val description: String?,

        @field:NotBlank(message = "주소는 필수 입력값입니다.")
        val address: String?,

        @field:NotBlank(message = "위도는 필수 입력값입니다.")
        val latitude: String?,

        @field:NotBlank(message = "경도는 필수 입력값입니다.")
        val longitude: String?,

        @field:NotNull(message = "지도 타입은 필수 입력값입니다.")
        val mapType: MapType
    ) {
        fun toEntity(member: Member): Map {
            return Map(name, description, address, latitude, longitude, mapType, member)
        }
    }
}