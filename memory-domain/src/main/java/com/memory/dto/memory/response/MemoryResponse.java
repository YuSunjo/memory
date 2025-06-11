package com.memory.dto.memory.response;

import com.memory.domain.memory.Memory;
import com.memory.domain.memory.MemoryType;
import com.memory.dto.map.response.MapResponse;
import com.memory.dto.member.response.MemberResponse;

import java.time.LocalDateTime;

public record MemoryResponse(
        Long id,
        String title,
        String content,
        String locationName,
        MemberResponse member,
        MapResponse map,
        MemoryType memoryType,
        LocalDateTime createDate
) {

    public static MemoryResponse from(Memory memory) {
        return new MemoryResponse(
                memory.getId(),
                memory.getTitle(),
                memory.getContent(),
                memory.getLocationName(),
                MemberResponse.from(memory.getMember()),
                MapResponse.from(memory.getMap()),
                memory.getMemoryType(),
                memory.getCreateDate()
        );
    }
}