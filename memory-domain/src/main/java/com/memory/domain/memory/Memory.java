package com.memory.domain.memory;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.comment.Comment;
import com.memory.domain.file.File;
import com.memory.domain.hashtag.MemoryHashTag;
import com.memory.domain.map.Map;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Memory extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Column(columnDefinition = "text")
    private String content;

    private String locationName;

    private LocalDate memorableDate;

    @Enumerated(EnumType.STRING)
    private MemoryType memoryType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "map_id")
    private Map map;

    @OneToMany(mappedBy = "memory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<File> files = new ArrayList<>();

    @OneToMany(mappedBy = "memory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "memory", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemoryHashTag> memoryHashTags = new ArrayList<>();

    @Builder
    public Memory(String title, String content, String locationName, LocalDate memorableDate, MemoryType memoryType, Member member, Map map) {
        this.title = title;
        this.content = content;
        this.locationName = locationName;
        this.memorableDate = memorableDate;
        this.memoryType = memoryType;
        this.member = member;
        this.map = map;
        this.files = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.memoryHashTags = new ArrayList<>();
    }

    public void update(String title, String content, String locationName, LocalDate memorableDate, MemoryType memoryType) {
        this.title = title;
        this.content = content;
        this.locationName = locationName;
        this.memorableDate = memorableDate;
        this.memoryType = memoryType;
    }

    public void addFile(File file) {
        this.files.add(file);
        file.updateMemory(this);
    }

    public void addFiles(List<File> files) {
        files.forEach(this::addFile);
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
    }

    public void addMemoryHashTag(MemoryHashTag memoryHashTag) {
        this.memoryHashTags.add(memoryHashTag);
        memoryHashTag.updateMemory(this);
    }

    public void addMemoryHashTags(List<MemoryHashTag> memoryHashTags) {
        memoryHashTags.forEach(this::addMemoryHashTag);
    }

    public void clearHashTags() {
        this.memoryHashTags.clear();
    }

    public List<String> getHashTagNames() {
        return memoryHashTags.stream()
                .filter(memoryHashTag -> !memoryHashTag.isDeleted())
                .map(memoryHashTag -> memoryHashTag.getHashTag().getName())
                .toList();
    }

    /**
     * 최상위 댓글 수 조회 (삭제되지 않은 댓글)
     */
    public long getTopLevelCommentsCount() {
        return comments.stream()
                .filter(comment -> !comment.isDeleted() && comment.isTopLevel())
                .count();
    }

    public long getCommentsCount() {
        return comments.stream()
                .filter(comment -> !comment.isDeleted())
                .count();
    }

    public boolean isPublic() {
        return memoryType == MemoryType.PUBLIC;
    }
}
