package com.memory.dto.file.response;

import com.memory.domain.file.File;
import com.memory.domain.file.FileType;

import java.time.LocalDateTime;

public record FileResponse(
        Long id,
        String originalFileName,
        String fileName,
        String fileUrl,
        FileType fileType,
        Long fileSize,
        Long memoryId,
        Long memberId,
        LocalDateTime createDate
) {

    public static FileResponse from(File file) {
        return new FileResponse(
                file.getId(),
                file.getOriginalFileName(),
                file.getFileName(),
                file.getFileUrl(),
                file.getFileType(),
                file.getFileSize(),
                file.getMemory() != null ? file.getMemory().getId() : null,
                file.getMember() != null ? file.getMember().getId() : null,
                file.getCreateDate()
        );
    }
}