package com.memory.service.upload;

import com.memory.component.storage.S3Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.net.URL;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "cloud.aws", name = "enabled", havingValue = "true")
public class S3Service {

    private final S3Client s3Client;
    private final S3Component s3Component;

    public String uploadFile(MultipartFile multipartFile, String fileName) {
        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Component.getS3().getBucket())
                    .key(fileName)
                    .contentType(multipartFile.getContentType())
                    .contentLength(multipartFile.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(multipartFile.getInputStream(), multipartFile.getSize()));

            return getFileUrl(fileName);
        } catch (IOException e) {
            log.error("Failed to upload file to S3", e);
            throw new RuntimeException("Failed to upload file to S3", e);
        }
    }

    public void deleteFile(String fileUrl) {
        try {
            String bucket = s3Component.getS3().getBucket();
            String fileName = fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(s3Component.getS3().getBucket())
                    .key(fileName)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
        } catch (Exception e) {
            log.error("Failed to delete file from S3", e);
            throw new RuntimeException("Failed to delete file from S3", e);
        }
    }

    private String getFileUrl(String fileName) {
        boolean isMinioEndpoint = s3Component.getS3().getEndpoint() != null &&
                !s3Component.getS3().getEndpoint().contains("amazonaws.com");

        if (isMinioEndpoint) {
            // MinIO 방식 URL 생성
            return s3Component.getS3().getEndpoint() + "/" +
                    s3Component.getS3().getBucket() + "/" +
                    fileName;
        } else {
            // AWS S3 방식 URL 생성
            GetUrlRequest getUrlRequest = GetUrlRequest.builder()
                    .bucket(s3Component.getS3().getBucket())
                    .key(fileName)
                    .build();
            URL url = s3Client.utilities().getUrl(getUrlRequest);
            return url.toString();
        }
    }
}
