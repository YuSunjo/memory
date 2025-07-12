package com.memory.domain.file;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import com.memory.domain.memory.Memory;
import jakarta.persistence.*;
import lombok.*;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class File extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;

    private String fileName;

    private String fileUrl;

    @Enumerated(EnumType.STRING)
    private FileType fileType;

    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id")
    private Memory memory;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public File(String originalFileName, String fileName, String fileUrl, FileType fileType, Long fileSize) {
        this.originalFileName = originalFileName;
        this.fileName = fileName;
        this.fileUrl = fileUrl;
        this.fileType = fileType;
        this.fileSize = fileSize;
    }

    public void updateMemory(Memory memory) {
        this.memory = memory;
    }

    public void updateMember(Member member) {
        this.member = member;
    }

    public boolean validateMember(Long memberId) {
        return this.getMember() != null && !this.getMember().getId().equals(memberId);
    }
}
