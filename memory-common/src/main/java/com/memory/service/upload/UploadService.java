package com.memory.service.upload;

import lombok.RequiredArgsConstructor;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UploadService {

    private final S3Service s3Service;

    public String uploadFile(MultipartFile multipartFile, String dirName) {
        UploadUtils.validate(multipartFile);
        String originalFilename = multipartFile.getOriginalFilename() == null ? "" : multipartFile.getOriginalFilename();
        String fileName = UploadUtils.createFileName(dirName, originalFilename);

        return s3Service.uploadFile(multipartFile, fileName);
    }

    public void deleteFile(String fileUrl) {
        s3Service.deleteFile(fileUrl);
    }

    public List<String> uploadFiles(List<MultipartFile> multipartFiles, String dirName) {
        multipartFiles.forEach(UploadUtils::validate);

        List<String> uploadedUrls = new ArrayList<>();
        try {
            int fileIndex = 1;
            for (MultipartFile file : multipartFiles) {
                String originalFilename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();

                String fileNameWithoutExt = FilenameUtils.removeExtension(originalFilename);
                String extension = FilenameUtils.getExtension(originalFilename);

                String numberedFilename = String.format("%s_%d.%s", fileNameWithoutExt, fileIndex++, extension);
                String fileName = UploadUtils.createFileName(dirName, numberedFilename);

                String fileUrl = s3Service.uploadFile(file, fileName);
                uploadedUrls.add(fileUrl);
            }

            return uploadedUrls;
        } catch (Exception e) {
            uploadedUrls.forEach(this::deleteFile);
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }

    }
}
