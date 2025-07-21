package com.memory.domain.comment;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import com.memory.domain.memory.Memory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@ToString(exclude = {"memory", "member", "parent", "children"})
@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text", nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer depth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memory_id", nullable = false)
    private Memory memory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Comment parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> children = new ArrayList<>();

    private Comment(String content, Memory memory, Member member, Comment parent) {
        this.content = content;
        this.memory = memory;
        this.member = member;
        this.parent = parent;
        this.depth = calculateDepth(parent);
        this.children = new ArrayList<>();
        
        validateDepth();
        
        if (parent != null) {
            parent.addChild(this);
        }
    }

    private Integer calculateDepth(Comment parent) {
        return parent == null ? 0 : parent.getDepth() + 1;
    }

    private void validateDepth() {
        if (this.depth > 1) {
            throw new IllegalArgumentException("댓글은 최대 2단계(대댓글)까지만 가능합니다.");
        }
    }

    public void updateContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("댓글 내용은 필수입니다.");
        }
        this.content = content;
    }

    private void addChild(Comment child) {
        this.children.add(child);
    }

    public boolean isTopLevel() {
        return this.depth == 0;
    }

    public boolean canHaveReply() {
        return this.depth < 1;
    }

    public boolean isAuthor(Member member) {
        return this.member.equals(member);
    }

    public long getActiveChildrenCount() {
        return children.stream()
                .filter(comment -> !comment.isDeleted())
                .count();
    }

    public void markAsDeleted() {
        if (hasActiveChildren()) {
            this.content = "삭제된 댓글입니다.";
        }
        this.updateDelete();
    }

    private boolean hasActiveChildren() {
        return getActiveChildrenCount() > 0;
    }

    public static Comment create(String content, Memory memory, Member member, Comment parent) {
        return new Comment(content, memory, member, parent);
    }
}
