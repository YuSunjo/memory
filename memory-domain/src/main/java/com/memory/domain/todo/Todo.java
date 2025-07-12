package com.memory.domain.todo;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.common.repeat.RepeatSetting;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@ToString
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Todo extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private LocalDateTime dueDate;

    private boolean completed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Embedded
    private RepeatSetting repeatSetting;

    @Builder
    private Todo(String title, String content, LocalDateTime dueDate, Member member, RepeatSetting repeatSetting) {
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.completed = false;
        this.member = member;
        this.repeatSetting = repeatSetting != null ? repeatSetting : RepeatSetting.none();
    }

    public static Todo create(String title, String content, LocalDateTime dueDate, Member member, RepeatSetting repeatSetting) {
        return new Todo(title, content, dueDate, member, repeatSetting);
    }

    public static Todo create(String title, String content, LocalDateTime dueDate, Member member) {
        return new Todo(title, content, dueDate, member, RepeatSetting.none());
    }

    public void update(String title, String content, LocalDateTime dueDate) {
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
    }

    public void update(String title, String content, LocalDateTime dueDate, RepeatSetting repeatSetting) {
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        updateRepeatSetting(repeatSetting);
    }

    public void updateRepeatSetting(RepeatSetting repeatSetting) {
        this.repeatSetting = repeatSetting != null ? repeatSetting : RepeatSetting.none();
    }

    public void complete() {
        this.completed = true;
    }

    public void incomplete() {
        this.completed = false;
    }

    public boolean isOwner(Member member) {
        if (member == null || this.member == null) {
            return true;
        }
        return !this.member.getId().equals(member.getId());
    }
}
