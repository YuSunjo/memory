package com.memory.service.file

import com.memory.domain.file.File
import com.memory.domain.file.repository.FileRepository
import com.memory.dto.file.FileRequest
import com.memory.dto.file.response.FileResponse
import com.memory.dto.file.response.FileResponse.Companion.from
import com.memory.exception.customException.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.stream.Collectors

@Service
class FileService(
    private val fileRepository: FileRepository,

) {
    @Transactional
    fun createFile(createRequest: FileRequest.Create): FileResponse {
        val file = createRequest.toEntity()

        val savedFile = fileRepository.save<File>(file)
        return from(savedFile)
    }

    @Transactional
    fun deleteFile(fileId: Long) {
        val file = fileRepository.findById(fileId)
            .orElseThrow{ NotFoundException("파일을 찾을 수 없습니다.") }

        file.updateDelete()
    }

    @Transactional
    fun createFileList(requestList: List<FileRequest.Create>): MutableList<FileResponse?> {
        val files = requestList.stream()
            .map { request: FileRequest.Create -> request.toEntity() }
            .collect(Collectors.toList())

        fileRepository.saveAll(files)
        return files.stream()
            .map { file: File -> from(file) }
            .collect(Collectors.toList())
    }
}