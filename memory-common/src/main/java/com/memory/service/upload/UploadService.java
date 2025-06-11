package com.memory.service.upload;

import com.memory.dto.UploadResponse;
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

    public UploadResponse uploadFile(MultipartFile multipartFile, String dirName) {
        UploadUtils.validate(multipartFile);
        String originalFilename = multipartFile.getOriginalFilename() == null ? "" : multipartFile.getOriginalFilename();
        String fileName = UploadUtils.createFileName(originalFilename);
        String fileUrl = dirName + "/" + fileName;
        String uploadFileUrl = s3Service.uploadFile(multipartFile, fileUrl);
        return UploadResponse.of(originalFilename, fileName, uploadFileUrl, multipartFile.getSize());
    }

    public void deleteFile(String fileUrl) {
        s3Service.deleteFile(fileUrl);
    }

    public List<UploadResponse> uploadFileList(List<MultipartFile> multipartFiles, String dirName) {
        multipartFiles.forEach(UploadUtils::validate);

        List<UploadResponse> uploadResponseList = new ArrayList<>();
        try {
            int fileIndex = 1;
            for (MultipartFile file : multipartFiles) {
                String originalFilename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();

                String fileNameWithoutExt = FilenameUtils.removeExtension(originalFilename);
                String extension = FilenameUtils.getExtension(originalFilename);

                String numberedFilename = String.format("%s_%d.%s", fileNameWithoutExt, fileIndex++, extension);
                String fileName = UploadUtils.createFileName(numberedFilename);
                String fileUrl = dirName + "/" + fileName;

                String uploadFileUrl = s3Service.uploadFile(file, fileUrl);
                UploadResponse uploadResponse = UploadResponse.of(originalFilename, fileName, uploadFileUrl, file.getSize());
                uploadResponseList.add(uploadResponse);
            }

            return uploadResponseList;
        } catch (Exception e) {
            throw new RuntimeException("파일 업로드 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}
