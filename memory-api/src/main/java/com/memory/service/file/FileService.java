package com.memory.service.file;

import com.memory.domain.file.File;
import com.memory.domain.file.repository.FileRepository;
import com.memory.dto.file.FileRequest;
import com.memory.dto.file.response.FileResponse;
import com.memory.exception.customException.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Transactional
    public FileResponse createFile(FileRequest.Create createRequest) {
        File file = createRequest.toEntity();

        File savedFile = fileRepository.save(file);
        return FileResponse.from(savedFile);
    }

    @Transactional
    public void deleteFile(Long fileId) {
        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("파일을 찾을 수 없습니다."));

        file.updateDelete();
    }

    @Transactional
    public List<FileResponse> createFileList(List<FileRequest.Create> requestList) {
        List<File> files = requestList.stream()
                .map(FileRequest.Create::toEntity)
                .collect(Collectors.toList());

        fileRepository.saveAll(files);
        return files.stream()
                .map(FileResponse::from)
                .collect(Collectors.toList());
    }
}