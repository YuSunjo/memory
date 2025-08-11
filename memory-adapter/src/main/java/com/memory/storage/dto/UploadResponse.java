package com.memory.storage.dto;

public record UploadResponse(
        String originalFileName,
        String fileName,
        String fileUrl,
        Long fileSize
) {
    public static UploadResponse of(String originalFilename, String fileName, String uploadFileUrl, long size) {
        return new UploadResponse(originalFilename, fileName, uploadFileUrl, size);
    }
}
