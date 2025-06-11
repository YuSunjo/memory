package com.memory.controller.file;

import com.memory.annotation.Auth;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.domain.file.FileType;
import com.memory.dto.file.response.FileResponse;
import com.memory.response.ServerResponse;
import com.memory.service.file.FileFacade;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Image", description = "Image API")
public class FileController {

    private final FileFacade fileFacade;

    @ApiOperations.SecuredApi(
            summary = "이미지 업로드",
            description = "이미지를 S3에 업로드합니다.",
            response = FileResponse.class
    )
    @Auth
    @PostMapping(value = "api/v1/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServerResponse<FileResponse> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "fileType", defaultValue = "MEMBER") FileType fileType) {
        return ServerResponse.success(fileFacade.uploadFile(file, fileType));
    }

    @Auth
    @ApiOperations.SecuredApi(
            summary = "이미지 삭제",
            description = "S3에서 이미지를 삭제합니다.",
            response = String.class
    )
    @DeleteMapping("api/v1/file/{fileId}")
    public ServerResponse<String> deleteImage(@PathVariable Long fileId) {
        fileFacade.deleteFile(fileId);
        return ServerResponse.OK;
    }

    @ApiOperations.SecuredApi(
            summary = "다중 이미지 업로드",
            description = "여러 이미지를 S3에 업로드합니다.",
            response = FileResponse.class
    )
    @Auth
    @PostMapping(value = "api/v1/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServerResponse<List<FileResponse>> uploadImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "fileType", defaultValue = "MEMORY") FileType fileType) {
        return ServerResponse.success(fileFacade.uploadFileList(files, fileType));
    }
}
