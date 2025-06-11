package com.memory.controller.upload;

import com.memory.annotation.Auth;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.response.ServerResponse;
import com.memory.service.upload.UploadService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Image", description = "Image API")
public class UploadController {

    private final UploadService uploadService;

    @ApiOperations.SecuredApi(
            summary = "이미지 업로드",
            description = "이미지를 S3에 업로드합니다.",
            response = String.class
    )
    @Auth
    @PostMapping(value = "api/v1/file", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServerResponse<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "directory", defaultValue = "images") String directory) {
        String fileUrl = uploadService.uploadFile(file, directory);
        return ServerResponse.success(fileUrl);
    }

    @Auth
    @ApiOperations.SecuredApi(
            summary = "이미지 삭제",
            description = "S3에서 이미지를 삭제합니다.",
            response = String.class
    )
    @DeleteMapping("api/v1/file")
    public ServerResponse<String> deleteImage(
            @RequestParam("fileUrl") String fileUrl) {
        uploadService.deleteFile(fileUrl);
        return ServerResponse.OK;
    }

    @ApiOperations.SecuredApi(
            summary = "다중 이미지 업로드",
            description = "여러 이미지를 S3에 업로드합니다.",
            response = List.class
    )
    @Auth
    @PostMapping(value = "api/v1/files", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ServerResponse<List<String>> uploadImages(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam(value = "directory", defaultValue = "files") String directory) {
        List<String> fileUrls = uploadService.uploadFiles(files, directory);
        return ServerResponse.success(fileUrls);
    }
}
