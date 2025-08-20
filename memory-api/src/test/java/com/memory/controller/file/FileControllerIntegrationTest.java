package com.memory.controller.file;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.memory.config.jwt.JwtTokenProvider;
import com.memory.domain.file.File;
import com.memory.domain.file.FileType;
import com.memory.domain.file.repository.FileRepository;
import com.memory.domain.member.Member;
import com.memory.domain.member.MemberType;
import com.memory.domain.member.repository.MemberRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private Member testMember;
    private File testFile;
    private String validToken;

    @BeforeEach
    void setUp() {
        String uniqueEmail = "test" + System.currentTimeMillis() + "@example.com";
        String encodedPassword = passwordEncoder.encode("password123");
        testMember = new Member("Test User", "testuser", uniqueEmail, encodedPassword, MemberType.MEMBER);
        testMember = memberRepository.save(testMember);
        
        validToken = "Bearer " + jwtTokenProvider.createAccessToken(testMember.getEmail());
        
        testFile = File.builder()
            .originalFileName("test.jpg")
            .fileName("test_file.jpg")
            .fileUrl("https://example.com/test.jpg")
            .fileType(FileType.MEMBER)
            .fileSize(1024L)
            .build();
        testFile.updateMember(testMember);
        testFile = fileRepository.save(testFile);
    }

    @Test
    @DisplayName("단일 파일 업로드 통합 테스트 - 성공")
    void uploadSingleFileIntegrationSuccess() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/v1/file")
                        .file(file)
                        .param("fileType", "MEMBER")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.originalFileName").value("test.jpg"))
                .andExpect(jsonPath("$.data.fileType").value("MEMBER"));
    }

    @Test
    @DisplayName("단일 파일 업로드 실패 - 인증 토큰 없음")
    void uploadSingleFileFailNoAuth() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/v1/file")
                        .file(file)
                        .param("fileType", "MEMBER"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("파일 삭제 통합 테스트 - 성공")
    void deleteFileIntegrationSuccess() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/file/{fileId}", testFile.getId())
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"));
    }

    @Test
    @DisplayName("파일 삭제 실패 - 인증 토큰 없음")
    void deleteFileFailNoAuth() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/file/{fileId}", testFile.getId()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("존재하지 않는 파일 삭제 시도")
    void deleteNonExistentFile() throws Exception {
        // When & Then
        mockMvc.perform(delete("/api/v1/file/{fileId}", 99999L)
                        .header("Authorization", validToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("다중 파일 업로드 통합 테스트 - 성공")
    void uploadMultipleFilesIntegrationSuccess() throws Exception {
        // Given
        MockMultipartFile file1 = new MockMultipartFile(
            "files", 
            "test1.jpg", 
            "image/jpeg", 
            "test image content 1".getBytes()
        );
        MockMultipartFile file2 = new MockMultipartFile(
            "files", 
            "test2.jpg", 
            "image/jpeg", 
            "test image content 2".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/v1/files")
                        .file(file1)
                        .file(file2)
                        .param("fileType", "MEMORY")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2));
    }

    @Test
    @DisplayName("다중 파일 업로드 실패 - 인증 토큰 없음")
    void uploadMultipleFilesFailNoAuth() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "files", 
            "test.jpg", 
            "image/jpeg", 
            "test image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/v1/files")
                        .file(file)
                        .param("fileType", "MEMORY"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("MEMORY 타입 파일 업로드 테스트")
    void uploadMemoryTypeFileIntegrationTest() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "memory.jpg", 
            "image/jpeg", 
            "memory image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/v1/file")
                        .file(file)
                        .param("fileType", "MEMORY")
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.fileType").value("MEMORY"));
    }

    @Test
    @DisplayName("기본 파일 타입으로 단일 파일 업로드 테스트")
    void uploadFileWithDefaultTypeIntegrationTest() throws Exception {
        // Given
        MockMultipartFile file = new MockMultipartFile(
            "file", 
            "default.jpg", 
            "image/jpeg", 
            "default image content".getBytes()
        );

        // When & Then
        mockMvc.perform(multipart("/api/v1/file")
                        .file(file)
                        .header("Authorization", validToken))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(200))
                .andExpect(jsonPath("$.data.fileType").value("MEMBER"));
    }

    @Test
    @DisplayName("빈 파일 업로드 실패 테스트")
    void uploadEmptyFileFailTest() throws Exception {
        // Given
        MockMultipartFile emptyFile = new MockMultipartFile(
            "file", 
            "empty.jpg", 
            "image/jpeg", 
            new byte[0]
        );

        // When & Then
        mockMvc.perform(multipart("/api/v1/file")
                        .file(emptyFile)
                        .param("fileType", "MEMBER")
                        .header("Authorization", validToken))
                .andExpect(status().isInternalServerError());
    }
}