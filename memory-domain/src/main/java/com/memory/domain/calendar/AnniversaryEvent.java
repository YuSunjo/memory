package com.memory.domain.calendar;

import com.memory.domain.member.Member;
import com.memory.domain.relationship.Relationship;
import com.memory.exception.customException.ValidationException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString(callSuper = true)
@Entity
@Getter
@DiscriminatorValue("ANNIVERSARY_EVENT")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AnniversaryEvent extends BaseCalendarEvent {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "relationship_id")
    private Relationship relationship;

    private Boolean isDday;

    private AnniversaryEvent(String title, String description, LocalDateTime startDateTime, 
                       LocalDateTime endDateTime, String location, Member member, 
                       Relationship relationship, Boolean isDday) {
        super(title, description, startDateTime, endDateTime, location, member);
        this.relationship = relationship;
        this.isDday = isDday;
    }

    // 기념일 생성 팩토리 메서드
    public static AnniversaryEvent create(String title, String description, 
                                   LocalDateTime startDateTime, LocalDateTime endDateTime, 
                                   String location, Member member, Relationship relationship, Boolean isDday) {
        if (relationship == null) {
            throw new ValidationException("기념일은 관계가 필요합니다.");
        }
        return new AnniversaryEvent(title, description, startDateTime, endDateTime, 
                              location, member, relationship, isDday);
    }

    // 기념일 생성 팩토리 메서드 (isDday 기본값 false)
    public static AnniversaryEvent create(String title, String description, 
                                   LocalDateTime startDateTime, LocalDateTime endDateTime, 
                                   String location, Member member, Relationship relationship) {
        return create(title, description, startDateTime, endDateTime, location, member, relationship, false);
    }

    // 일정 관련자 확인 메서드
    public boolean isRelatedTo(Member member) {
        return this.relationship != null && 
               (this.relationship.getRelatedMember().getId().equals(member.getId()) || 
                this.relationship.getMember().getId().equals(member.getId()));
    }

    // 일정 접근 권한 확인 메서드 (기념일은 소유자와 관계에 있는 회원만 접근 가능)
    @Override
    public void validateAccessPermission(Member member) {
        if (isOwner(member) && !isRelatedTo(member)) {
            throw new ValidationException("이 일정에 접근할 권한이 없습니다.");
        }
    }

    // 기념일 수정 메서드 (isDday 포함)
    public void update(String title, String description, LocalDateTime startDateTime, 
                      LocalDateTime endDateTime, String location, Boolean isDday) {
        super.update(title, description, startDateTime, endDateTime, location);
        this.isDday = isDday;
    }
}
