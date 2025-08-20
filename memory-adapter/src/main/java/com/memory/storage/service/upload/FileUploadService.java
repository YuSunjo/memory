package com.memory.storage.service.upload;

import com.memory.storage.dto.UploadResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileUploadService {
    UploadResponse uploadFile(MultipartFile multipartFile, String dirName);
    void deleteFile(String fileUrl);
    List<UploadResponse> uploadFileList(List<MultipartFile> multipartFiles, String dirName);
}