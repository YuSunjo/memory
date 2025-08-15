package com.memory.storage.service.upload;

import com.memory.component.storage.S3Component;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

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
            String fileName = extractFileNameFromUrl(fileUrl);
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

    private String extractFileNameFromUrl(String fileUrl) {
        String cdnEndpoint = s3Component.getS3().getCdnEndpoint();
        
        // CloudFront URL인 경우
        if (cdnEndpoint != null && fileUrl.startsWith(cdnEndpoint)) {
            return fileUrl.substring(cdnEndpoint.length() + 1);
        }
        
        // 기존 S3 URL인 경우
        String bucket = s3Component.getS3().getBucket();
        if (fileUrl.contains(bucket)) {
            return fileUrl.substring(fileUrl.indexOf(bucket) + bucket.length() + 1);
        }
        
        // URL의 마지막 경로 부분을 파일명으로 추출
        return fileUrl.substring(fileUrl.lastIndexOf('/') + 1);
    }

    private String getFileUrl(String fileName) {
        if (s3Component.getS3().getCdnEndpoint() != null && !s3Component.getS3().getCdnEndpoint().isEmpty()) {
            // CDN 엔드포인트가 설정되어 있으면 CloudFront URL 반환
            return s3Component.getS3().getCdnEndpoint() + "/" + s3Component.getS3().getBucket() + "/" + fileName;
        }
        return s3Component.getS3().getEndpoint() + "/" +
                s3Component.getS3().getBucket() + "/" +
                fileName;
    }
}
