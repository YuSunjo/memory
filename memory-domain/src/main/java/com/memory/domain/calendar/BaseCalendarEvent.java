package com.memory.domain.calendar;

import com.memory.domain.BaseTimeEntity;
import com.memory.domain.member.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Entity
@Getter
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "event_type")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class BaseCalendarEvent extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private String location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    protected BaseCalendarEvent(String title, String description, LocalDateTime startDateTime, 
                              LocalDateTime endDateTime, String location, Member member) {
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
        this.member = member;
    }

    // 일정 수정 메서드
    public void update(String title, String description, LocalDateTime startDateTime, 
                      LocalDateTime endDateTime, String location) {
        this.title = title;
        this.description = description;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.location = location;
    }

    // 일정 소유자 확인 메서드
    public boolean isOwner(Member member) {
        return !this.member.getId().equals(member.getId());
    }

    // 일정 접근 권한 확인 메서드
    public abstract void validateAccessPermission(Member member);
}