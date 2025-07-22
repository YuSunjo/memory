package com.memory.dto.memory.response;

import com.memory.domain.memory.Memory;
import com.memory.domain.memory.MemoryType;
import com.memory.dto.file.response.FileResponse;
import com.memory.dto.map.response.MapResponse;
import com.memory.dto.member.response.MemberResponse;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record MemoryResponse(
        Long id,
        String title,
        String content,
        String locationName,
        MemberResponse member,
        MapResponse map,
        MemoryType memoryType,
        List<FileResponse> files,
        LocalDateTime createDate,
        Long commentsCount
) {

    public static MemoryResponse from(Memory memory) {
        List<FileResponse> fileResponses = memory.getFiles().stream()
                .filter(file -> file.getDeleteDate() == null)
                .map(FileResponse::from)
                .collect(Collectors.toList());

        return new MemoryResponse(
                memory.getId(),
                memory.getTitle(),
                memory.getContent(),
                memory.getLocationName(),
                MemberResponse.from(memory.getMember()),
                MapResponse.from(memory.getMap()),
                memory.getMemoryType(),
                fileResponses,
                memory.getCreateDate(),
                memory.getCommentsCount()
        );
    }
}
