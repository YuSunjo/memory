package com.memory.dto.memory.response

import com.memory.domain.memory.Memory
import com.memory.domain.memory.MemoryType
import com.memory.dto.file.response.FileResponse
import com.memory.dto.map.response.MapResponse
import com.memory.dto.member.response.MemberResponse
import java.time.LocalDate
import java.time.LocalDateTime

data class MemoryResponse(
    val id: Long?,
    val title: String?,
    val content: String?,
    val locationName: String?,
    val memorableDate: LocalDate?,
    val member: MemberResponse,
    val map: MapResponse?,
    val memoryType: MemoryType,
    val files: List<FileResponse>,
    val hashTagNames: List<String>,
    val createDate: LocalDateTime?,
    val commentsCount: Long
) {
    companion object {
        @JvmStatic
        fun from(memory: Memory): MemoryResponse {
            val fileResponses = memory.files
                .filter { it.deleteDate == null }
                .map { FileResponse.from(it) }

            return MemoryResponse(
                id = memory.id,
                title = memory.title,
                content = memory.content,
                locationName = memory.locationName,
                memorableDate = memory.memorableDate,
                member = MemberResponse.from(memory.member),
                map = memory.map ?.let { MapResponse.from(it) },
                memoryType = memory.memoryType,
                files = fileResponses,
                hashTagNames = memory.getHashTagNames(),
                createDate = memory.createDate,
                commentsCount = memory.getCommentsCount()
            )
        }
    }
}