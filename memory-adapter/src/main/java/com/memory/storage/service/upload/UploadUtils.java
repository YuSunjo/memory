package com.memory.storage.service.upload;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class UploadUtils {
    private static final long allowedFileSize = 10 * 1024 * 1024;
    private static final List<String> allowedExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp", "webp");

    public static void validateFileType(String originalFilename) {
        String extension = FilenameUtils.getExtension(originalFilename).toLowerCase();

        if (!allowedExtensions.contains(extension)) {
            throw new IllegalArgumentException("지원하지 않는 파일 형식입니다. (지원 확장자: jpg, jpeg, png, gif, bmp, webp)");
        }
    }

    public static void validateFileSize(long size) {
        if (size > allowedFileSize) {
            throw new IllegalArgumentException("파일 크기가 너무 큽니다. 최대 10MB까지 지원합니다.");
        }
    }

    public static String createFileName(String originalFilename) {
        String filenameWithoutExtension = originalFilename.substring(0, originalFilename.lastIndexOf("."));
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

        LocalDateTime now = LocalDateTime.now();
        String timestamp = now.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        return filenameWithoutExtension + "_" + timestamp + extension;
    }

    public static void validateMultipartFile(MultipartFile multipartFile) {
        if (multipartFile == null || multipartFile.isEmpty() || multipartFile.getSize() == 0 || multipartFile.getOriginalFilename() == null || multipartFile.getOriginalFilename().isEmpty()) {
            throw new IllegalArgumentException("파일이 비어있습니다.");
        }
    }

    public static void validate(MultipartFile multipartFile) {
        validateMultipartFile(multipartFile);
        validateFileType(multipartFile.getOriginalFilename());
        validateFileSize(multipartFile.getSize());
    }
}
