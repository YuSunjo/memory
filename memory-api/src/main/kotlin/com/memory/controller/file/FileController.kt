package com.memory.controller.file

import com.memory.annotation.Auth
import com.memory.annotation.swagger.ApiOperations.SecuredApi
import com.memory.domain.file.FileType
import com.memory.dto.file.response.FileResponse
import com.memory.response.ServerResponse
import com.memory.response.ServerResponse.Companion.success
import com.memory.service.file.FileFacade
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@Tag(name = "Image", description = "Image API")
class FileController(
    private val fileFacade: FileFacade,
) {
    @SecuredApi(summary = "이미지 업로드", description = "이미지를 S3에 업로드합니다.", response = FileResponse::class)
    @Auth
    @PostMapping(value = ["api/v1/file"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImage(
        @RequestParam("file") file: MultipartFile?,
        @RequestParam(value = "fileType", defaultValue = "MEMBER") fileType: FileType
    ): ServerResponse<FileResponse?> {
        return success(fileFacade.uploadFile(file, fileType))
    }

    @Auth
    @SecuredApi(summary = "이미지 삭제", description = "S3에서 이미지를 삭제합니다.", response = String::class)
    @DeleteMapping("api/v1/file/{fileId}")
    fun deleteImage(@PathVariable fileId: Long): ServerResponse<String> {
        fileFacade.deleteFile(fileId)
        return ServerResponse.OK
    }

    @SecuredApi(summary = "다중 이미지 업로드", description = "여러 이미지를 S3에 업로드합니다.", response = FileResponse::class)
    @Auth
    @PostMapping(value = ["api/v1/files"], consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadImages(
        @RequestParam("files") files: MutableList<MultipartFile?>?,
        @RequestParam(value = "fileType", defaultValue = "MEMORY") fileType: FileType
    ): ServerResponse<MutableList<FileResponse?>?> {
        return success(fileFacade.uploadFileList(files, fileType))
    }
}
