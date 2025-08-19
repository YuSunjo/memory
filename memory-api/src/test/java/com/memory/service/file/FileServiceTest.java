package com.memory.service.file;

import com.memory.domain.file.File;
import com.memory.domain.file.FileType;
import com.memory.domain.file.repository.FileRepository;
import com.memory.dto.file.FileRequest;
import com.memory.dto.file.response.FileResponse;
import com.memory.exception.customException.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("FileService 테스트")
class FileServiceTest {

    @Mock
    private FileRepository fileRepository;

    @InjectMocks
    private FileService fileService;

    private FileRequest.Create createRequest;
    private File mockFile;

    @BeforeEach
    void setUp() {
        createRequest = new FileRequest.Create(
                "test.jpg",
                "stored_test.jpg",
                "https://s3.bucket/stored_test.jpg",
                FileType.MEMORY,
                1024L,
                1L,
                1L
        );

        mockFile = new File("test.jpg", "stored_test.jpg", "https://s3.bucket/stored_test.jpg", FileType.MEMORY, 1024L);
        // ID 설정을 위해 reflection 사용하거나 테스트에서는 ID 체크 제외
    }

    @Test
    @DisplayName("파일 생성 성공")
    void createFile_Success() {
        // Given
        when(fileRepository.save(any(File.class))).thenReturn(mockFile);

        // When
        FileResponse response = fileService.createFile(createRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.originalFileName()).isEqualTo("test.jpg");
        assertThat(response.fileName()).isEqualTo("stored_test.jpg");
        assertThat(response.fileSize()).isEqualTo(1024L);
        assertThat(response.fileType()).isEqualTo(FileType.MEMORY);
        assertThat(response.fileUrl()).isEqualTo("https://s3.bucket/stored_test.jpg");

        verify(fileRepository, times(1)).save(any(File.class));
    }

    @Test
    @DisplayName("파일 삭제 성공")
    void deleteFile_Success() {
        // Given
        when(fileRepository.findById(1L)).thenReturn(Optional.of(mockFile));

        // When
        fileService.deleteFile(1L);

        // Then
        verify(fileRepository, times(1)).findById(1L);
        assertThat(mockFile.isDeleted()).isTrue();
    }

    @Test
    @DisplayName("존재하지 않는 파일 삭제 시 예외 발생")
    void deleteFile_NotFound_ThrowsException() {
        // Given
        when(fileRepository.findById(anyLong())).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> fileService.deleteFile(999L))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("파일을 찾을 수 없습니다.");

        verify(fileRepository, times(1)).findById(999L);
    }

    @Test
    @DisplayName("다중 파일 생성 성공")
    void createFileList_Success() {
        // Given
        FileRequest.Create request1 = createRequest;
        FileRequest.Create request2 = new FileRequest.Create(
                "test2.jpg",
                "stored_test2.jpg", 
                "https://s3.bucket/stored_test2.jpg",
                FileType.MEMORY,
                2048L,
                1L,
                1L
        );

        File file1 = mockFile;
        File file2 = new File("test2.jpg", "stored_test2.jpg", "https://s3.bucket/stored_test2.jpg", FileType.MEMORY, 2048L);

        List<FileRequest.Create> requestList = Arrays.asList(request1, request2);
        List<File> mockFiles = Arrays.asList(file1, file2);

        when(fileRepository.saveAll(anyList())).thenReturn(mockFiles);

        // When
        List<FileResponse> responses = fileService.createFileList(requestList);

        // Then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).originalFileName()).isEqualTo("test.jpg");
        assertThat(responses.get(1).originalFileName()).isEqualTo("test2.jpg");
        assertThat(responses.get(0).fileSize()).isEqualTo(1024L);
        assertThat(responses.get(1).fileSize()).isEqualTo(2048L);

        verify(fileRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("빈 파일 리스트 생성")
    void createFileList_EmptyList() {
        // Given
        List<FileRequest.Create> emptyList = List.of();

        when(fileRepository.saveAll(anyList())).thenReturn(List.of());

        // When
        List<FileResponse> responses = fileService.createFileList(emptyList);

        // Then
        assertThat(responses).isEmpty();
        verify(fileRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("파일 삭제 후 삭제 상태 확인")
    void deleteFile_CheckDeletedStatus() {
        // Given
        File file = new File("test.jpg", "stored_test.jpg", "https://s3.bucket/stored_test.jpg", FileType.MEMORY, 1024L);

        when(fileRepository.findById(1L)).thenReturn(Optional.of(file));

        // When
        fileService.deleteFile(1L);

        // Then
        assertThat(file.isDeleted()).isTrue();
        assertThat(file.getDeleteDate()).isNotNull();
    }
}