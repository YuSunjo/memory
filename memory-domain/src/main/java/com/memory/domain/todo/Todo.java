package com.memory.domain.todo;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import com.memory.domain.routine.Routine;
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

    private boolean isRoutine; // 루틴에서 생성된 Todo인지 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "routine_id")
    private Routine routine; // 이 Todo가 어떤 루틴에서 생성되었는지 (nullable)

    @Builder
    private Todo(String title, String content, LocalDateTime dueDate, Member member, Routine routine, boolean isRoutine) {
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.completed = false;
        this.member = member;
        this.routine = routine;
        this.isRoutine = isRoutine;
    }

    public static Todo create(String title, String content, LocalDateTime dueDate, Member member) {
        return new Todo(title, content, dueDate, member, null, false);
    }

    // 루틴에서 생성되는 Todo
    public static Todo createFromRoutine(Routine routine, LocalDateTime dueDate, Member member) {
        return new Todo(routine.getTitle(), routine.getContent(), dueDate, member, routine, true);
    }

    public void update(String title, String content, LocalDateTime dueDate) {
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
    }

    public void complete() {
        this.completed = true;
    }

    public void incomplete() {
        this.completed = false;
    }

    public boolean isOwner(Member member) {
        if (member == null || this.member == null) {
            return false;
        }
        return this.member.getId().equals(member.getId());
    }

    public boolean isFromRoutine() {
        return isRoutine && routine != null;
    }
}
