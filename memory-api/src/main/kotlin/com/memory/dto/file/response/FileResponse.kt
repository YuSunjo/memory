package com.memory.dto.file.response

import com.memory.domain.file.File
import com.memory.domain.file.FileType
import java.time.LocalDateTime

data class FileResponse(
    val id: Long?,
    val originalFileName: String,
    val fileName: String,
    val fileUrl: String,
    val fileType: FileType,
    val fileSize: Long,
    val memoryId: Long?,
    val memberId: Long?,
    val createDate: LocalDateTime?
) {
    companion object {
        @JvmStatic
        fun from(file: File): FileResponse =
            FileResponse(
                id = file.id,
                originalFileName = file.originalFileName,
                fileName = file.fileName,
                fileUrl = file.fileUrl,
                fileType = file.fileType,
                fileSize = file.fileSize,
                memoryId = file.memory?.id,
                memberId = file.member?.id,
                createDate = file.createDate
            )
    }
}