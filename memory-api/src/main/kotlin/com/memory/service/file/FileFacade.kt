package com.memory.service.file

import com.memory.domain.file.FileType
import com.memory.dto.file.FileRequest
import com.memory.dto.file.response.FileResponse
import com.memory.storage.dto.UploadResponse
import com.memory.storage.service.upload.FileUploadService
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class FileFacade(
    private val uploadService: FileUploadService,
    private val fileService: FileService,
) {
    fun uploadFile(file: MultipartFile?, fileType: FileType): FileResponse? {
        val uploadResponse = uploadService.uploadFile(file, fileType.directory)
        val request = FileRequest.Create(
            uploadResponse.originalFileName,
            uploadResponse.fileName,
            uploadResponse.fileUrl,
            fileType,
            uploadResponse.fileSize,
            null,
            null
        )
        return fileService.createFile(request)
    }

    fun deleteFile(fileId: Long) {
        fileService.deleteFile(fileId)
    }

    fun uploadFileList(files: MutableList<MultipartFile?>?, fileType: FileType): MutableList<FileResponse?>? {
        val uploadResponseList = uploadService.uploadFileList(files, fileType.directory)
        val requestList = uploadResponseList.stream()
            .map<FileRequest.Create?> { uploadResponse: UploadResponse? ->
                FileRequest.Create(
                    uploadResponse!!.originalFileName,
                    uploadResponse.fileName,
                    uploadResponse.fileUrl,
                    fileType,
                    uploadResponse.fileSize,
                    null,
                    null
                )
            }
            .toList()
        return fileService.createFileList(requestList)
    }
}
