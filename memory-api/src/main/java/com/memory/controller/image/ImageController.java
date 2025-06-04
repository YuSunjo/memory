package com.memory.controller.image;

import com.memory.annotation.Auth;
import com.memory.annotation.swagger.ApiOperations;
import com.memory.response.ServerResponse;
import com.memory.service.S3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Tag(name = "Image", description = "Image API")
public class ImageController {

    private final S3Service s3Service;

    @ApiOperations.SecuredApi(
            summary = "이미지 업로드",
            description = "이미지를 S3에 업로드합니다.",
            response = String.class
    )
    @Auth
    @PostMapping("api/v1/image")
    public ServerResponse<String> uploadImage(
            @RequestParam("image") MultipartFile image,
            @RequestParam(value = "directory", defaultValue = "images") String directory) {
        String imageUrl = s3Service.uploadFile(image, directory);
        return ServerResponse.success(imageUrl);
    }

    @Auth
    @ApiOperations.SecuredApi(
            summary = "이미지 삭제",
            description = "S3에서 이미지를 삭제합니다.",
            response = String.class
    )
    @DeleteMapping("api/v1/image")
    public ServerResponse<String> deleteImage(
            @RequestParam("imageUrl") String imageUrl) {

        s3Service.deleteFile(imageUrl);
        return ServerResponse.OK;
    }
}
