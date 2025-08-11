package com.memory.dto.file;

import com.memory.domain.file.File;
import com.memory.domain.file.FileType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

public class FileRequest {

    @Getter
    public static class Create {
        @NotBlank(message = "원본 파일 이름은 필수 입력값입니다.")
        private String originalFileName;

        @NotBlank(message = "파일 이름은 필수 입력값입니다.")
        private String fileName;

        @NotBlank(message = "파일 URL은 필수 입력값입니다.")
        private String fileUrl;

        @NotNull(message = "파일 타입은 필수 입력값입니다.")
        private FileType fileType;

        @NotNull(message = "파일 크기는 필수 입력값입니다.")
        private Long fileSize;

        @NotNull(message = "메모리 ID는 필수 입력값입니다.")
        private Long memoryId;

        @NotNull(message = "멤버 ID는 필수 입력값입니다.")
        private Long memberId;

        public Create(String originalFileName, String fileName, String fileUrl, FileType fileType, Long fileSize, Long memoryId, Long memberId) {
            this.originalFileName = originalFileName;
            this.fileName = fileName;
            this.fileUrl = fileUrl;
            this.fileType = fileType;
            this.fileSize = fileSize;
            this.memoryId = memoryId;
            this.memberId = memberId;
        }

        public Create(MultipartFile file, String fileUrl, FileType fileType, Long memoryId, Long memberId) {
            this.originalFileName = file.getOriginalFilename();
            this.fileName = file.getName();
            this.fileUrl = fileUrl;
            this.fileType = fileType;
            this.fileSize = file.getSize();
            this.memoryId = memoryId;
            this.memberId = memberId;
        }

        public File toEntity() {
            return new File(originalFileName, fileName, fileUrl, fileType, fileSize);
        }
    }
}