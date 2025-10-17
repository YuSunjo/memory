package com.memory.dto.file

import com.memory.domain.file.File
import com.memory.domain.file.FileType
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.web.multipart.MultipartFile

class FileRequest {

    data class Create(
        @field:NotBlank(message = "원본 파일 이름은 필수 입력값입니다.")
        val originalFileName: String,

        @field:NotBlank(message = "파일 이름은 필수 입력값입니다.")
        val fileName: String,

        @field:NotBlank(message = "파일 URL은 필수 입력값입니다.")
        val fileUrl: String,

        @field:NotNull(message = "파일 타입은 필수 입력값입니다.")
        val fileType: FileType,

        @field:NotNull(message = "파일 크기는 필수 입력값입니다.")
        val fileSize: Long,

        @field:NotNull(message = "메모리 ID는 필수 입력값입니다.")
        val memoryId: Long?,

        @field:NotNull(message = "멤버 ID는 필수 입력값입니다.")
        val memberId: Long?
    ) {
        // MultipartFile 기반 보조 생성자
        constructor(
            file: MultipartFile,
            fileUrl: String,
            fileType: FileType,
            memoryId: Long,
            memberId: Long
        ) : this(
            originalFileName = file.originalFilename ?: file.name,
            fileName = file.name,
            fileUrl = fileUrl,
            fileType = fileType,
            fileSize = file.size,
            memoryId = memoryId,
            memberId = memberId
        )

        fun toEntity(): File =
            File(originalFileName, fileName, fileUrl, fileType, fileSize)
    }
}