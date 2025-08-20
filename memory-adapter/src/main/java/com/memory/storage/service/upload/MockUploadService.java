package com.memory.storage.service.upload;

import com.memory.storage.dto.UploadResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@ConditionalOnProperty(prefix = "cloud.aws", name = "enabled", havingValue = "false")
public class MockUploadService implements FileUploadService {

    public UploadResponse uploadFile(MultipartFile multipartFile, String dirName) {
        UploadUtils.validate(multipartFile);
        String originalFilename = multipartFile.getOriginalFilename() == null ? "" : multipartFile.getOriginalFilename();
        String fileName = UploadUtils.createFileName(originalFilename);
        String fileUrl = "https://mock-storage.com/" + dirName + "/" + fileName;
        return UploadResponse.of(originalFilename, fileName, fileUrl, multipartFile.getSize());
    }

    public void deleteFile(String fileUrl) {
        // Mock implementation - do nothing
    }

    public List<UploadResponse> uploadFileList(List<MultipartFile> multipartFiles, String dirName) {
        multipartFiles.forEach(UploadUtils::validate);

        List<UploadResponse> uploadResponseList = new ArrayList<>();
        int fileIndex = 1;
        for (MultipartFile file : multipartFiles) {
            String originalFilename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
            String fileName = UploadUtils.createFileName(originalFilename);
            String fileUrl = "https://mock-storage.com/" + dirName + "/" + fileName;
            UploadResponse uploadResponse = UploadResponse.of(originalFilename, fileName, fileUrl, file.getSize());
            uploadResponseList.add(uploadResponse);
        }
        return uploadResponseList;
    }
}