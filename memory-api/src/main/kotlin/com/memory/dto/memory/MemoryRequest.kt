package com.memory.dto.memory

import com.memory.domain.map.Map
import com.memory.domain.member.Member
import com.memory.domain.memory.Memory
import com.memory.domain.memory.MemoryType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.time.LocalDate

class MemoryRequest {

    data class Create(
        @field:NotBlank(message = "제목은 필수 입력값입니다.")
        val title: String,

        @field:NotBlank(message = "내용은 필수 입력값입니다.")
        val content: String,

        @field:NotBlank(message = "위치 이름은 필수 입력값입니다.")
        val locationName: String,

        @field:NotNull(message = "메모리 날짜는 필수 입력값입니다.")
        val memorableDate: LocalDate,

        @field:NotNull(message = "지도 ID는 필수 입력값입니다.")
        val mapId: Long,

        @field:NotNull(message = "메모리 타입은 필수 입력값입니다.(PUBLIC, PRIVATE, COUPLE_PUBLIC, COUPLE_PRIVATE)")
        val memoryType: MemoryType,

        val fileIdList: List<Long> = emptyList(),
        val hashTagList: List<String> = emptyList()
    ) {
        fun toEntity(member: Member, map: Map): Memory =
            Memory(title, content, locationName, memorableDate, memoryType, member, map)
    }

    data class Update(
        @field:NotBlank(message = "제목은 필수 입력값입니다.")
        val title: String,

        @field:NotBlank(message = "내용은 필수 입력값입니다.")
        val content: String,

        @field:NotBlank(message = "위치 이름은 필수 입력값입니다.")
        val locationName: String,

        val memorableDate: LocalDate?, // 자바에서도 필수 아님

        @field:NotNull(message = "메모리 타입은 필수 입력값입니다.(PUBLIC, PRIVATE, COUPLE_PUBLIC, COUPLE_PRIVATE)")
        val memoryType: MemoryType,

        val fileIdList: List<Long> = emptyList(),
        val hashTagList: List<String> = emptyList()
    )

    data class GetByMember(
        val lastMemoryId: Long? = null,
        val size: Int = 10,
        val memoryType: MemoryType = MemoryType.PUBLIC
    )

    data class GetByPublic(
        val lastMemoryId: Long? = null,
        val size: Int = 10
    )
}