package com.memory.service.file;

import com.memory.domain.file.FileType;
import com.memory.dto.UploadResponse;
import com.memory.dto.file.FileRequest;
import com.memory.dto.file.response.FileResponse;
import com.memory.service.upload.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileFacade {

    private final UploadService uploadService;
    private final FileService fileService;

    public FileResponse uploadFile(MultipartFile file, FileType fileType) {
        UploadResponse uploadResponse = uploadService.uploadFile(file, fileType.getDirectory());
        FileRequest.Create request = new FileRequest.Create(
                uploadResponse.originalFileName(),
                uploadResponse.fileName(),
                uploadResponse.fileUrl(),
                fileType,
                uploadResponse.fileSize(),
                null,
                null
        );
        return fileService.createFile(request);
    }

    public void deleteFile(Long fileId) {
        fileService.deleteFile(fileId);
    }

    public List<FileResponse> uploadFileList(List<MultipartFile> files, FileType fileType) {
        List<UploadResponse> uploadResponseList = uploadService.uploadFileList(files, fileType.getDirectory());
        List<FileRequest.Create> requestList = uploadResponseList.stream()
                .map(uploadResponse -> new FileRequest.Create(
                        uploadResponse.originalFileName(),
                        uploadResponse.fileName(),
                        uploadResponse.fileUrl(),
                        fileType,
                        uploadResponse.fileSize(),
                        null,
                        null
                ))
                .toList();
        return fileService.createFileList(requestList);
    }
}
